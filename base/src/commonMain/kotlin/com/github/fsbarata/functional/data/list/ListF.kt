@file:Suppress("NOTHING_TO_INLINE")

package com.github.fsbarata.functional.data.list

import com.github.fsbarata.functional.Context
import com.github.fsbarata.functional.control.*
import com.github.fsbarata.functional.data.*
import com.github.fsbarata.functional.data.maybe.Optional
import com.github.fsbarata.functional.data.sequence.SequenceF
import com.github.fsbarata.functional.data.set.SetF
import com.github.fsbarata.io.Serializable

/**
 * Functional list extension.
 *
 * Guaranteed to be immutable by construction. The wrapped list is either immutable or unreachable
 */
@Suppress("OVERRIDE_BY_INLINE")
class ListF<out A> internal constructor(private val wrapped: List<A>): List<A> by wrapped,
	ImmutableList<A>,
	Serializable,
	MonadZip<ListContext, A>,
	MonadPlus<ListContext, A>,
	Traversable<ListContext, A>,
	RandomAccess,
	Semigroup<ListF<@UnsafeVariance A>> {
	override val scope get() = ListF

	constructor(size: Int, init: (index: Int) -> A): this(List(size, init))

	override fun subList(fromIndex: Int, toIndex: Int) = ListF(wrapped.subList(fromIndex, toIndex))

	operator fun plus(other: @UnsafeVariance A): ListF<A> = ListF(wrapped + other)
	operator fun plus(other: Iterable<@UnsafeVariance A>): ListF<A> = ListF(wrapped + other)

	fun plusElementNel(other: @UnsafeVariance A): NonEmptyList<A> = toNel()?.plus(other) ?: NonEmptyList.just(other)

	override inline fun <B> map(f: (A) -> B): ListF<B> =
		asIterable().mapTo(ArrayList(size), f).f()

	override inline fun onEach(f: (A) -> Unit): ListF<A> {
		forEach(f)
		return this
	}

	inline fun <B> mapIndexed(f: (index: Int, A) -> B): ListF<B> =
		asIterable().mapIndexedTo(ArrayList(size), f).f()

	inline fun onEachIndexed(f: (index: Int, A) -> Unit): ListF<A> {
		forEachIndexed(f)
		return this
	}

	override infix fun <B> ap(ff: Context<ListContext, (A) -> B>): ListF<B> =
		ff.asList.flatMap(::map)

	override inline fun <B, R> lift2(fb: Context<ListContext, B>, f: (A, B) -> R): ListF<R> =
		bind { a -> fb.asList.map { b -> f(a, b) } }

	override inline infix fun <B> bind(f: (A) -> Context<ListContext, B>): ListF<B> =
		flatMap { f(it).asList }

	inline fun <B> flatMap(f: (A) -> List<B>): ListF<B> = when (size) {
		0 -> empty()
		1 -> fromList(f(get(0)))
		else -> {
			val result = ArrayList<B>()
			for (element in this) {
				val list = f(element)
				when (list.size) {
					0 -> {}
					1 -> result.add(list[0])
					else -> result.addAll(list)
				}
			}
			fromList(result)
		}
	}

	override inline fun filter(predicate: (A) -> Boolean): ListF<A> =
		asIterable().filter(predicate).f()

	override inline fun partition(predicate: (A) -> Boolean): Pair<ListF<A>, ListF<A>> {
		val p = asIterable().partition(predicate)
		return Pair(p.first.f(), p.second.f())
	}

	override inline fun <B: Any> mapNotNull(f: (A) -> B?): ListF<B> =
		asIterable().mapNotNull(f).f()

	override inline fun <B: Any> mapNotNone(f: (A) -> Optional<B>): ListF<B> =
		mapNotNull { f(it).orNull() }

	override inline fun <R> foldL(initialValue: R, accumulator: (R, A) -> R): R =
		asIterable().fold(initialValue, accumulator)

	override inline fun <R> foldR(initialValue: R, accumulator: (A, R) -> R): R =
		(this as List<A>).foldRight(initialValue, accumulator)

	override inline fun <M> foldMap(monoid: Monoid<M>, f: (A) -> M): M =
		asIterable().foldMap(monoid, f)

	override inline fun <B, R> zipWith(other: Context<ListContext, B>, f: (A, B) -> R): ListF<R> =
		zip(other.asList, f).f()

	override inline fun <F, B> traverse(
		appScope: Applicative.Scope<F>,
		f: (A) -> Context<F, B>,
	): Context<F, ListF<B>> =
		asIterable().traverse(appScope, f)

	override fun combineWith(other: Context<ListContext, @UnsafeVariance A>) = plus(other.asList)
	override fun concatWith(other: ListF<@UnsafeVariance A>) = plus(other)

	override fun toString() = wrapped.toString()
	override fun equals(other: Any?) = wrapped == other
	override fun hashCode() = wrapped.hashCode()

	@Deprecated("Unnecessary call to toList()", replaceWith = ReplaceWith("this"))
	override fun toList() = this
	override fun toSetF(): SetF<A> = SetF.fromIterable(wrapped)
	fun asSequence(): SequenceF<A> = SequenceF.fromIterable(wrapped)

	inline fun reversed(): ListF<A> = asReversed()
	fun asReversed(): ListF<A> = ListF(wrapped.asReversed())

	fun toNel(): NonEmptyList<A>? = when {
		isEmpty() -> null
		wrapped is NonEmptyList -> wrapped
		else -> NonEmptyList(this[0], drop(1))
	}

	fun startWith(other: Iterable<@UnsafeVariance A>): ListF<A> = ListF(other + this)

	fun uncons(): Pair<A, ListF<A>>? =
		if (isEmpty()) null
		else Pair(this[0], drop(1))

	inline fun windowed(size: Int, step: Int = 1, partialWindows: Boolean = false): ListF<ListF<A>> =
		windowed(size, step, partialWindows, id())

	inline fun windowedNel(size: Int, step: Int = 1, partialWindows: Boolean = false): ListF<NonEmptyList<A>> =
		windowed(size, step, partialWindows) { it.toNel() ?: throw NoSuchElementException() }

	inline fun chunked(size: Int): ListF<ListF<A>> = chunked(size, id())
	inline fun chunkedNel(size: Int): ListF<NonEmptyList<A>> =
		chunked(size) { it.toNel() ?: throw NoSuchElementException() }

	companion object:
		MonadPlus.Scope<ListContext>,
		MonadZip.Scope<ListContext>,
		Traversable.Scope<ListContext> {
		private val EMPTY = ListF<Nothing>(emptyList())
		override fun <A> empty(): ListF<A> = EMPTY
		override fun <A> just(a: A): ListF<A> = ListF(NonEmptyList.just(a))
		fun <A> of(vararg items: A): ListF<A> = ListF(listOf(*items))

		fun <A> monoid(): Monoid<ListF<A>> = monoid(empty())

		override fun <A> fromIterable(iterable: Iterable<A>): ListF<A> = when (iterable) {
			is ListF -> iterable
			is ImmutableList<A> -> ListF(iterable)
			else -> fromSequence(iterable.asSequence())
		}

		override fun <A> fromSequence(sequence: Sequence<A>) = ListF(sequence.toList())
		override inline fun <A> fromList(list: List<A>) =
			if (list.isEmpty()) empty()
			else fromIterable(list)

		override inline fun <A> fromOptional(optional: Optional<A>): ListF<A> =
			optional.fold(ifEmpty = ::empty, ifSome = ::just)
	}
}

inline fun <A> List<A>.f() = ListF.fromList(this)
inline fun <A> List<A>.toListF() = ListF.fromList(this)
inline fun <A> Iterable<A>.toListF() = ListF.fromIterable(this)
inline fun <A> Sequence<A>.toListF() = ListF.fromSequence(this)

fun <A> List<A>.asFoldable(): Foldable<A> = f()

internal typealias ListContext = ListF<*>

val <A> Context<ListContext, A>.asList: ListF<A>
	get() = this as ListF<A>


operator fun <A, R> Lift1<A, R>.invoke(
	list: List<A>,
): ListF<R> = ListF(list.map(f))

operator fun <A, B, R> Lift2<A, B, R>.invoke(
	list1: List<A>,
	list2: List<B>,
): ListF<R> = app(ListF(list1), ListF(list2)).asList

operator fun <A, B, C, R> Lift3<A, B, C, R>.invoke(
	list1: List<A>,
	list2: List<B>,
	list3: List<C>,
): ListF<R> = app(ListF(list1), ListF(list2), ListF(list3)).asList

operator fun <A, B, C, D, R> Lift4<A, B, C, D, R>.invoke(
	list1: List<A>,
	list2: List<B>,
	list3: List<C>,
	list4: List<D>,
): ListF<R> = app(ListF(list1), ListF(list2), ListF(list3), ListF(list4)).asList

fun <A, R> liftList(f: (A) -> R): (List<A>) -> ListF<R> = lift(f)::invoke
fun <A, B, R> liftList2(f: (A, B) -> R): (List<A>, List<B>) -> ListF<R> = lift2(f)::invoke
fun <A, B, C, R> liftList3(f: (A, B, C) -> R): (List<A>, List<B>, List<C>) -> ListF<R> = lift3(f)::invoke

