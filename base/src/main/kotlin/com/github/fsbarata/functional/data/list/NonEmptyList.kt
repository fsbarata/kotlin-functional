package com.github.fsbarata.functional.data.list

import com.github.fsbarata.functional.Context
import com.github.fsbarata.functional.control.*
import com.github.fsbarata.functional.data.Semigroup
import com.github.fsbarata.functional.data.Traversable
import com.github.fsbarata.functional.data.collection.NonEmptyCollection
import com.github.fsbarata.functional.data.collection.flattenToList
import com.github.fsbarata.functional.data.partial
import com.github.fsbarata.functional.data.sequence.NonEmptySequence
import com.github.fsbarata.functional.data.set.NonEmptySet
import com.github.fsbarata.functional.utils.*
import java.io.Serializable
import kotlin.random.Random

/**
 * A NonEmpty list.
 *
 * By definition, this object is guaranteed to have at least one item.
 */
@Suppress("OVERRIDE_BY_INLINE")
class NonEmptyList<out A> private constructor(
	override val head: A,
	override val tail: List<A>,
): List<A>,
	NonEmptyCollection<A>,
	Serializable,
	MonadZip<NonEmptyContext, A>,
	Traversable<NonEmptyContext, A>,
	Comonad<NonEmptyContext, A>,
	Semigroup<NonEmptyList<@UnsafeVariance A>> {
	override val scope get() = Companion

	override val size: Int = 1 + tail.size

	@Deprecated("Non empty list cannot be empty", replaceWith = ReplaceWith("false"))
	override fun isEmpty() = false

	override fun contains(element: @UnsafeVariance A): Boolean = super.contains(element)
	override fun containsAll(elements: Collection<@UnsafeVariance A>) = super.containsAll(elements)

	override fun get(index: Int): A =
		if (index == 0) head
		else tail[index - 1]

	override fun indexOf(element: @UnsafeVariance A) =
		if (head == element) 0
		else (tail.indexOf(element) + 1).takeIf { it != 0 } ?: -1

	override fun lastIndexOf(element: @UnsafeVariance A) =
		(tail.lastIndexOf(element) + 1).takeIf { it != 0 }
			?: if (head == element) 0 else -1

	override fun iterator(): NonEmptyIterator<A> = super.iterator()

	override fun subList(fromIndex: Int, toIndex: Int): List<A> = when {
		fromIndex == 0 && toIndex == 0 -> emptyList()
		fromIndex == 0 -> NonEmptyList(head, tail.subList(0, toIndex - 1))
		else -> tail.subList(fromIndex - 1, toIndex - 1)
	}

	override inline fun <B> map(f: (A) -> B): NonEmptyList<B> = of(f(head), tail.map(f))

	override fun <B> ap(ff: Applicative<NonEmptyContext, (A) -> B>): NonEmptyList<B> =
		ff.asNel.flatMap(this::map)

	override inline fun <B, R> lift2(fb: Applicative<NonEmptyContext, B>, f: (A, B) -> R): NonEmptyList<R> =
		flatMap { a -> fb.asNel.map(f.partial(a)) }

	override inline infix fun <B> bind(f: (A) -> Context<NonEmptyContext, B>): NonEmptyList<B> =
		flatMap { f(it).asNel }

	inline fun <B> flatMap(f: (A) -> NonEmptyList<B>): NonEmptyList<B> {
		val mappedHead = f(head)
		return of(mappedHead.head, mappedHead.tail + tail.flatMap(f))
	}

	override inline fun <R> foldR(initialValue: R, accumulator: (A, R) -> R): R =
		foldRight(initialValue, accumulator)

	operator fun plus(other: @UnsafeVariance A) = NonEmptyList(head, tail + other)
	operator fun plus(other: Iterable<@UnsafeVariance A>) = NonEmptyList(head, tail + other)

	override inline fun <B, R> zipWith(other: MonadZip<NonEmptyContext, B>, f: (A, B) -> R): NonEmptyList<R> {
		val otherNel = other.asNel
		return of(f(head, otherNel.head), tail.zip(otherNel.tail, f))
	}

	fun reversed() = tail.asReversed().nonEmpty()?.plus(head) ?: this

	fun distinct() = NonEmptyList(head, (tail.toSet() - head).toList())
	inline fun <K> distinctBy(selector: (A) -> K): NonEmptyList<A> {
		val set = HashSet<K>()
		set.add(selector(head))
		return of(
			head,
			tail.filter { set.add(selector(it)) }
		)
	}

	override fun listIterator(): ListIterator<A> = LambdaListIterator(size) { get(it) }
	override fun listIterator(index: Int): ListIterator<A> = LambdaListIterator(size, index) { get(it) }

	override fun extract(): A = head
	override fun <B> extend(f: (Comonad<NonEmptyContext, A>) -> B): NonEmptyList<B> = coflatMap(f)

	fun <B> coflatMap(f: (NonEmptyList<A>) -> B): NonEmptyList<B> {
		val newHead = f(this)
		return of(
			newHead,
			(tail.toNel() ?: return just(newHead)).coflatMap(f)
		)
	}

	override inline fun <F, B> traverse(
		appScope: Applicative.Scope<F>,
		f: (A) -> Applicative<F, B>,
	): Applicative<F, NonEmptyList<B>> =
		tail.traverse(appScope, f)
			.lift2(f(head), List<B>::startWithItem)

	override fun combineWith(other: NonEmptyList<@UnsafeVariance A>) = this + other

	fun <K: Comparable<K>> sortedBy(selector: (A) -> K): NonEmptyList<A> =
		(this as List<A>).sortedBy(selector).toNelUnsafe()

	fun sortedWith(comparator: Comparator<@UnsafeVariance A>): NonEmptyList<A> =
		(this as List<A>).sortedWith(comparator).toNelUnsafe()

	override fun equals(other: Any?): Boolean {
		if (this === other) return true
		if (other !is List<*>) return false
		return listEquals(this, other)
	}

	override fun hashCode() = head.hashCode() + tail.hashCode()

	override fun toString() =
		joinToString(prefix = "[", postfix = "]")

	companion object: Monad.Scope<NonEmptyContext>, Traversable.Scope<NonEmptyContext> {
		override fun <A> just(a: A) = of(a, emptyList())
		fun <T> of(head: T, others: List<T>) = NonEmptyList(head, others)
	}
}

internal typealias NonEmptyContext = NonEmptyList<*>

val <A> Context<NonEmptyContext, A>.asNel get() = this as NonEmptyList<A>

fun <A> nelOf(head: A, vararg tail: A): NonEmptyList<A> = NonEmptyList.of(head, tail.toList())

fun <A> List<A>.startWithItem(item: A): NonEmptyList<A> = NonEmptyList.of(item, this)

fun <A> List<A>.nonEmpty(): NonEmptyList<A>? = toNel()
fun <A> Iterable<A>.toNel(): NonEmptyList<A>? {
	return when (this) {
		is NonEmptyList<A> -> this
		else -> iterator().nonEmpty()?.toNel()
	}
}

private fun <A> Iterable<A>.toNelUnsafe() = toNel() ?: throw NoSuchElementException()


operator fun <A, B, R> Lift2<A, B, R>.invoke(
	list1: NonEmptyList<A>,
	list2: NonEmptyList<B>,
): NonEmptyList<R> = app(list1, list2).asNel

operator fun <A, B, C, R> Lift3<A, B, C, R>.invoke(
	list1: NonEmptyList<A>,
	list2: NonEmptyList<B>,
	list3: NonEmptyList<C>,
): NonEmptyList<R> = app(list1, list2, list3).asNel

operator fun <A, B, C, D, R> Lift4<A, B, C, D, R>.invoke(
	list1: NonEmptyList<A>,
	list2: NonEmptyList<B>,
	list3: NonEmptyList<C>,
	list4: NonEmptyList<D>,
): NonEmptyList<R> = app(list1, list2, list3, list4).asNel