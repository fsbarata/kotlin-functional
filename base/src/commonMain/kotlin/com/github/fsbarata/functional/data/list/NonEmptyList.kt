@file:Suppress("NOTHING_TO_INLINE")

package com.github.fsbarata.functional.data.list

import com.github.fsbarata.functional.Context
import com.github.fsbarata.functional.control.*
import com.github.fsbarata.functional.data.*
import com.github.fsbarata.functional.data.collection.NonEmptyCollection
import com.github.fsbarata.functional.data.maybe.invoke
import com.github.fsbarata.functional.utils.LambdaListIterator
import com.github.fsbarata.functional.utils.listEquals
import com.github.fsbarata.functional.utils.toNel
import com.github.fsbarata.io.Serializable

/**
 * A NonEmpty list.
 *
 * By definition, this object is guaranteed to have at least one item.
 */
@Suppress("OVERRIDE_BY_INLINE")
class NonEmptyList<out A>(
	override val head: A,
	override val tail: ListF<A>,
): List<A>,
	NonEmptyCollection<A>,
	ImmutableList<A>,
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

	override fun indexOf(element: @UnsafeVariance A): Int =
		if (head == element) 0
		else {
			val tailIndex = tail.indexOf(element)
			if (tailIndex == -1) -1
			else tailIndex + 1
		}

	override fun lastIndexOf(element: @UnsafeVariance A): Int {
		return when (val tailIndex = tail.lastIndexOf(element)) {
			-1 -> if (head == element) 0 else -1
			else -> tailIndex + 1
		}
	}

	override fun iterator(): Iterator<A> = super.iterator()

	override fun subList(fromIndex: Int, toIndex: Int): ListF<A> = when {
		fromIndex == 0 && toIndex == 0 -> ListF.empty()
		fromIndex == 0 -> ListF.fromList(NonEmptyList(head, tail.subList(0, toIndex - 1)))
		else -> tail.subList(fromIndex - 1, toIndex - 1)
	}

	override inline fun <B> map(f: (A) -> B): NonEmptyList<B> = of(f(head), tail.map(f))

	override inline fun onEach(f: (A) -> Unit): NonEmptyList<A> {
		forEach(f)
		return this
	}

	inline fun <B> mapIndexed(f: (index: Int, A) -> B): NonEmptyList<B> =
		of(f(0, head), tail.mapIndexed { index, item -> f(index + 1, item) })

	override fun <B> ap(ff: Context<NonEmptyContext, (A) -> B>): NonEmptyList<B> =
		ff.asNel.flatMap(this::map)

	override inline fun <B, R> lift2(fb: Context<NonEmptyContext, B>, f: (A, B) -> R): NonEmptyList<R> =
		flatMap { a -> fb.asNel.map(f.partial(a)) }

	override inline infix fun <B> bind(f: (A) -> Context<NonEmptyContext, B>): NonEmptyList<B> =
		flatMap { f(it).asNel }

	inline fun <B> flatMap(f: (A) -> NonEmptyList<B>): NonEmptyList<B> {
		val mappedHead = f(head)
		return of(mappedHead.head, mappedHead.tail + tail.flatMap(f))
	}

	inline fun <B> flatMapIndexed(f: (index: Int, A) -> NonEmptyList<B>): NonEmptyList<B> {
		val mappedHead = f(0, head)
		return of(
			mappedHead.head,
			mappedHead.tail + tail.flatMapIndexed { index, item -> f(index + 1, item) },
		)
	}

	override inline fun <R> foldR(initialValue: R, accumulator: (A, R) -> R): R =
		foldRight(initialValue, accumulator)

	operator fun plus(other: @UnsafeVariance A) = of(head, tail + other)
	operator fun plus(other: Iterable<@UnsafeVariance A>) = NonEmptyList(head, tail + other)

	fun startWith(other: Iterable<@UnsafeVariance A>) = other.toNel()?.plus(this) ?: this

	fun uncons(): Pair<A, NonEmptyList<A>?> = Pair(head, tail.toNel())

	override inline fun <B, R> zipWith(other: Context<NonEmptyContext, B>, f: (A, B) -> R): NonEmptyList<R> {
		val otherNel = other.asNel
		return of(f(head, otherNel.head), tail.zipWith(otherNel.tail, f))
	}

	fun reversed(): NonEmptyList<A> = tail.asReversed().toNel()?.plus(head) ?: this

	fun distinct(): NonEmptyList<A> = toNes().toNel()
	inline fun <K> distinctBy(selector: (A) -> K): NonEmptyList<A> {
		val set = HashSet<K>()
		set.add(selector(head))
		return of(
			head,
			tail.filter { set.add(selector(it)) },
		)
	}

	override fun listIterator(): ListIterator<A> = LambdaListIterator(size) { get(it) }
	override fun listIterator(index: Int): ListIterator<A> = LambdaListIterator(size, index) { get(it) }

	override fun extract(): A = head
	override fun <B> extend(f: (Comonad<NonEmptyContext, A>) -> B): NonEmptyList<B> = coflatMap(f)

	fun <B> coflatMap(f: (NonEmptyList<A>) -> B): NonEmptyList<B> {
		val newHead = f(this)
		val tailNel = tail.toNel() ?: return just(newHead)
		return NonEmptyList(newHead, ListF(tailNel.coflatMap(f)))
	}

	override inline fun <F, B> traverse(
		appScope: Applicative.Scope<F>,
		f: (A) -> Context<F, B>,
	): Context<F, NonEmptyList<B>> =
		appScope.lift2(f(head), tail.traverse(appScope, f), ::of)

	inline fun <F, B> traverse(
		f: (A) -> Applicative<F, B>,
	): Context<F, NonEmptyList<B>> {
		val mappedHead = f(head)
		return mappedHead.lift2(
			tail.traverse(mappedHead.scope, f),
			::of
		)
	}

	override fun concatWith(other: NonEmptyList<@UnsafeVariance A>) = this + other

	override fun equals(other: Any?): Boolean {
		if (this === other) return true
		if (other !is List<*>) return false
		return listEquals(this, other)
	}

	override fun hashCode() = head.hashCode() + tail.hashCode()

	@Deprecated("Unnecessary call to toNel()", replaceWith = ReplaceWith("this"))
	override fun toNel() = this

	override fun toString() =
		joinToString(prefix = "[", postfix = "]")

	inline fun windowed(size: Int, step: Int = 1, partialWindows: Boolean = false): ListF<ListF<A>> =
		windowed(size, step, partialWindows, id())

	inline fun windowedNel(size: Int, step: Int = 1, partialWindows: Boolean = false): ListF<NonEmptyList<A>> =
		toList().windowedNel(size, step, partialWindows)

	companion object: MonadZip.Scope<NonEmptyContext>, Traversable.Scope<NonEmptyContext> {
		override fun <A> just(a: A) = NonEmptyList(a, ListF.empty())
		fun <T> of(head: T, others: List<T>) = NonEmptyList(head, others.f())
	}
}

internal typealias NonEmptyContext = NonEmptyList<*>

val <A> Context<NonEmptyContext, A>.asNel get() = this as NonEmptyList<A>
fun <A, R> F1<Context<NonEmptyContext, A>, Context<NonEmptyContext, R>>.asNel(): F1<Context<NonEmptyContext, A>, NonEmptyList<R>> =
	Context<NonEmptyContext, R>::asNel compose this

fun <A> nelOf(head: A, vararg tail: A): NonEmptyList<A> = NonEmptyList.of(head, tail.asList())

fun <A> List<A>.startWithItem(item: A): NonEmptyList<A> = NonEmptyList.of(item, this)
fun <A> List<A>.startWithNel(nel: NonEmptyList<A>): NonEmptyList<A> = nel + this

inline fun <A> List<A>.nonEmpty(): NonEmptyList<A>? = toNel()
fun <A> Iterable<A>.toNel(): NonEmptyList<A>? {
	return when (this) {
		is NonEmptyList<A> -> this
		is ListF<A> -> toNel()
		else -> iterator().toNel()
	}
}

fun <A> Sequence<A>.toNel(): NonEmptyList<A>? = iterator().toNel()


operator fun <A, R> Lift1<A, R>.invoke(
	list: Context<NonEmptyContext, A>,
): NonEmptyList<R> = fmap(NonEmptyList, list).asNel

operator fun <A, B, R> Lift2<A, B, R>.invoke(
	list1: Context<NonEmptyContext, A>,
	list2: Context<NonEmptyContext, B>,
): NonEmptyList<R> = app(NonEmptyList, list1, list2).asNel

operator fun <A, B, C, R> Lift3<A, B, C, R>.invoke(
	list1: Context<NonEmptyContext, A>,
	list2: Context<NonEmptyContext, B>,
	list3: Context<NonEmptyContext, C>,
): NonEmptyList<R> = app(NonEmptyList, list1, list2, list3).asNel

operator fun <A, B, C, D, R> Lift4<A, B, C, D, R>.invoke(
	list1: Context<NonEmptyContext, A>,
	list2: Context<NonEmptyContext, B>,
	list3: Context<NonEmptyContext, C>,
	list4: Context<NonEmptyContext, D>,
): NonEmptyList<R> = app(NonEmptyList, list1, list2, list3, list4).asNel

fun <A, R> liftNel(f: (A) -> R): (Context<NonEmptyContext, A>) -> NonEmptyList<R> = lift(f)::invoke
fun <A, B, R> liftNel2(f: (A, B) -> R): (Context<NonEmptyContext, A>, Context<NonEmptyContext, B>) -> NonEmptyList<R> =
	lift2(f)::invoke

fun <A, B, C, R> liftNel3(f: (A, B, C) -> R): (Context<NonEmptyContext, A>, Context<NonEmptyContext, B>, Context<NonEmptyContext, C>) -> NonEmptyList<R> =
	lift3(f)::invoke

@Suppress("NOTHING_TO_INLINE")
inline fun <F, A> NonEmptyList<Context<F, A>>.sequenceA(appScope: Applicative.Scope<F>): Context<F, NonEmptyList<A>> =
	traverse(appScope, ::id)

@Suppress("NOTHING_TO_INLINE")
inline fun <F, A> NonEmptyList<Applicative<F, A>>.sequenceA(): Context<F, NonEmptyList<A>> =
	traverse(head.scope, ::id)
