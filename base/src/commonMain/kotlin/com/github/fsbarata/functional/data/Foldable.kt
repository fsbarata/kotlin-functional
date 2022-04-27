package com.github.fsbarata.functional.data

import com.github.fsbarata.functional.Context
import com.github.fsbarata.functional.control.Alternative
import com.github.fsbarata.functional.data.list.ListF
import com.github.fsbarata.functional.data.list.NonEmptyList
import com.github.fsbarata.functional.data.monoid.Dual
import com.github.fsbarata.functional.data.monoid.Endo
import com.github.fsbarata.functional.data.monoid.dual
import com.github.fsbarata.functional.data.monoid.endoMonoid
import com.github.fsbarata.functional.data.sequence.NonEmptySequence

/**
 * Foldable structure
 *
 * Can fold left or right as well as map within the accumulation.
 *
 * Minimum definition: foldL or foldMap
 */
interface Foldable<out A> {
	/**
	 * Fold the structure from the left
	 */
	fun <R> foldL(initialValue: R, accumulator: (R, A) -> R): R =
		foldMap(
			endoMonoid<R>().dual(),
			accumulator.flip().curry() composeForward ::Endo composeForward ::Dual
		).get(initialValue)

	/**
	 * Fold the structure from the right
	 */
	fun <R> foldR(initialValue: R, accumulator: (A, R) -> R): R =
		foldMap(
			endoMonoid(),
			accumulator.curry() composeForward ::Endo
		)(initialValue)

	/**
	 * Fold the structure by mapping to a monoidal value
	 */
	fun <M> foldMap(monoid: Monoid<M>, f: (A) -> M): M =
		foldL(monoid.empty) { r, a -> monoid.combine(r, f(a)) }
}

fun <A, R> Foldable<A>.scanL(initialValue: R, accumulator: (R, A) -> R): NonEmptyList<R> =
	foldL(Pair(initialValue, NonEmptySequence.just(initialValue))) { (carry, nes), v ->
		val newValue = accumulator(carry, v)
		Pair(newValue, nes + newValue)
	}.second
		.toList()

fun <A, R> Foldable<A>.scanR(initialValue: R, accumulator: (A, R) -> R): NonEmptyList<R> =
	foldR(Pair(initialValue, NonEmptySequence.just(initialValue))) { v, (carry, nes) ->
		val newValue = accumulator(v, carry)
		Pair(newValue, nes + newValue)
	}.second
		.toList()

fun <A> Foldable<A>.fold(monoid: Monoid<A>) = foldMap(monoid, id())
fun <A: Semigroup<A>> Foldable<A>.foldL(initialValue: A) = foldL(initialValue, ::concat)
fun <A: Semigroup<A>> Foldable<A>.foldR(initialValue: A) = foldR(initialValue, ::concat)
fun <A: Semigroup<A>> Foldable<A>.scanL(initialValue: A): NonEmptyList<A> = scanL(initialValue, ::concat)
fun <A: Semigroup<A>> Foldable<A>.scanR(initialValue: A): NonEmptyList<A> = scanR(initialValue, ::concat)


fun <A> Foldable<A>.toList(): ListF<A> = foldMap(ListF.monoid()) { ListF.just(it) }

fun <F, A> Foldable<Context<F, A>>.asum(scope: Alternative.Scope<F>): Context<F, A> =
	foldL(scope.empty(), scope::combine)

fun <A> Iterable<A>.fold(monoid: Monoid<A>): A = foldMap(monoid, id())

@Suppress("OVERRIDE_BY_INLINE")
class FoldableIterable<A>(val iterable: Iterable<A>): Foldable<A>, Iterable<A> by iterable {
	override inline fun <R> foldL(initialValue: R, accumulator: (R, A) -> R) =
		iterable.fold(initialValue, accumulator)

	override inline fun <M> foldMap(monoid: Monoid<M>, f: (A) -> M) =
		iterable.foldMap(monoid, f)
}

fun <A> Iterable<A>.asFoldable() = FoldableIterable(this)

fun <A, R> Iterable<A>.foldL(initialValue: R, accumulator: (R, A) -> R): R = fold(initialValue, accumulator)
inline fun <A, M> Iterable<A>.foldMap(monoid: Monoid<M>, f: (A) -> M): M =
	fold(monoid.empty) { r, a -> monoid.combine(r, f(a)) }

fun <A: Semigroup<A>> Iterable<A>.foldL(initialValue: A): A = fold(initialValue, ::concat)
