package com.github.fsbarata.functional.data

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

fun <A: Semigroup<A>> Foldable<A>.fold(monoid: Monoid<A>) = foldMap(monoid, id())