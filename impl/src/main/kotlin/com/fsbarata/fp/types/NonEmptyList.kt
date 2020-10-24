package com.fsbarata.fp.types

import com.fsbarata.fp.concepts.Context
import com.fsbarata.fp.concepts.Foldable
import com.fsbarata.fp.concepts.Monad
import com.fsbarata.utils.iterators.*
import java.io.Serializable
import kotlin.random.Random

/**
 * A NonEmpty list.
 *
 * By definition, this object is guaranteed to have at least one item.
 */
class NonEmptyList<out A> private constructor(
	override val head: A,
	override val tail: List<A>,
): List<A>,
   Monad<NonEmptyList<*>, A>,
   Foldable<A>,
   NonEmptyIterable<A>,
   Serializable {
	override val scope get() = Companion

	override val size: Int get() = 1 + tail.size

	override fun get(index: Int): A =
		if (index == 0) head
		else tail[index - 1]

	@Deprecated("Non empty list cannot be empty", replaceWith = ReplaceWith("false"))
	override fun isEmpty() = false

	fun first() = head

	@Deprecated("Non empty list always has a first", replaceWith = ReplaceWith("first()"))
	fun firstOrNull(): Nothing = throw UnsupportedOperationException()
	fun last() = if (tail.isEmpty()) head else tail.last()

	@Deprecated("Non empty list always has a last", replaceWith = ReplaceWith("last()"))
	fun lastOrNull(): Nothing = throw UnsupportedOperationException()

	@Deprecated("Non empty list always has a random", replaceWith = ReplaceWith("random()"))
	fun randomOrNull(): Nothing = throw UnsupportedOperationException()

	@Deprecated("Non empty list always has a random", replaceWith = ReplaceWith("random()"))
	fun randomOrNull(random: Random): Nothing = throw UnsupportedOperationException()

	override fun contains(element: @UnsafeVariance A) = head == element || tail.contains(element)
	override fun containsAll(elements: Collection<@UnsafeVariance A>) = elements.all(this::contains)

	override fun indexOf(element: @UnsafeVariance A) =
		if (head == element) 0
		else (tail.indexOf(element) + 1).takeIf { it != 0 } ?: -1

	override fun lastIndexOf(element: @UnsafeVariance A) =
		(tail.lastIndexOf(element) + 1).takeIf { it != 0 }
			?: if (head == element) 0 else -1

	override fun iterator(): NonEmptyIterator<A> =
		NonEmptyIterator(head, tail.iterator())

	override fun subList(fromIndex: Int, toIndex: Int): List<A> = when {
		fromIndex == 0 && toIndex == 0 -> emptyList()
		fromIndex == 0 -> NonEmptyList(head, tail.subList(0, toIndex - 1))
		else -> tail.subList(fromIndex - 1, toIndex - 1)
	}

	override inline fun <B> map(f: (A) -> B): NonEmptyList<B> = of(f(head), tail.map(f))

	override fun <B> bind(f: (A) -> Context<NonEmptyList<*>, B>): NonEmptyList<B> =
		flatMap { f(it).asNel }

	inline fun <B> flatMap(f: (A) -> NonEmptyList<B>): NonEmptyList<B> = map(f).flatten()

	inline fun <B> flatMapIterable(f: (A) -> List<B>): List<B> = f(head) + tail.flatMap(f)

	operator fun plus(other: @UnsafeVariance A) = NonEmptyList(head, tail + other)
	operator fun plus(other: Iterable<@UnsafeVariance A>) = NonEmptyList(head, tail + other)

	fun reversed() = tail.asReversed().nonEmpty()?.plus(head) ?: this

	fun <R: Comparable<R>> maxOf(selector: (A) -> R): R =
		tail.maxOfOrNull(selector)?.coerceAtLeast(selector(head)) ?: selector(head)

	fun <R: Comparable<R>> minOf(selector: (A) -> R): R =
		tail.minOfOrNull(selector)?.coerceAtMost(selector(head)) ?: selector(head)

	fun distinct() = NonEmptyList(head, (tail.toSet() - head).toList())
	fun <K> distinctBy(selector: (A) -> K): NonEmptyList<A> {
		val set = HashSet<K>()
		set.add(selector(head))
		return NonEmptyList(
			head,
			tail.filter { set.add(selector(it)) }
		)
	}

	fun asSequence() = NonEmptySequence { iterator() }

	override fun listIterator(): ListIterator<A> = LambdaListIterator(size) { get(it) }
	override fun listIterator(index: Int): ListIterator<A> = LambdaListIterator(size, index) { get(it) }

	override fun equals(other: Any?): Boolean {
		if (this === other) return true
		if (other !is List<*>) return false
		return listEquals(this, other)
	}

	override fun hashCode() = head.hashCode() + tail.hashCode()

	companion object: Monad.Scope<NonEmptyList<*>> {
		override fun <A> just(a: A) = of(a, emptyList())
		fun <T> of(head: T, vararg others: T) = of(head, others.toList())
		fun <T> of(head: T, others: List<T>) = NonEmptyList(head, others)
	}
}

val <A> Context<NonEmptyList<*>, A>.asNel get() = this as NonEmptyList<A>

fun <A> nelOf(head: A): NonEmptyList<A> = NonEmptyList.just(head)
fun <A> nelOf(head: A, vararg tail: A): NonEmptyList<A> = NonEmptyList.of(head, *tail)

fun <A> List<A>.startWithItem(item: A) = nelOf(item, this)

fun <A> List<A>.nonEmpty(): NonEmptyList<A>? = toNel()
fun <A> Iterable<A>.toNel(): NonEmptyList<A>? {
	return when {
		this is NonEmptyList<A> -> this
		else -> iterator().nonEmpty()?.toNel()
	}
}

operator fun <A> Iterable<A>.plus(other: NonEmptyList<A>) =
	toNel()?.plus(other) ?: other

