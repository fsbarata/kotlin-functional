package com.github.fsbarata.functional.data

import com.github.fsbarata.functional.control.Alternative
import com.github.fsbarata.functional.data.list.ListF
import com.github.fsbarata.functional.data.list.NonEmptyList
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
fun <A: Semigroup<A>> Foldable<A>.foldL(initialValue: A) = foldL(initialValue, ::combine)
fun <A: Semigroup<A>> Foldable<A>.foldR(initialValue: A) = foldR(initialValue, ::combine)
fun <A: Semigroup<A>> Foldable<A>.scanL(initialValue: A): NonEmptyList<A> = scanL(initialValue, ::combine)
fun <A: Semigroup<A>> Foldable<A>.scanR(initialValue: A): NonEmptyList<A> = scanR(initialValue, ::combine)


fun <A> Foldable<A>.toList(): ListF<A> = foldMap(ListF.monoid()) { ListF.just(it) }

fun <F, A> Foldable<Alternative<F, A>>.asum(scope: Alternative.Scope<F>) =
	foldL(scope.empty(), Alternative<F, A>::associateWith)
