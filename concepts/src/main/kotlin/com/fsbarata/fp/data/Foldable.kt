package com.fsbarata.fp.data

interface Foldable<out A> {
	/**
	 * Fold the structure from the left
	 */
	fun <R> foldL(initialValue: R, accumulator: (R, A) -> R): R =
		foldMap(
			endoMonoid<R>().dual(),
			accumulator.flip().curry()
		)(initialValue)

	/**
	 * Fold the structure from the right
	 */
	fun <R> foldR(initialValue: R, accumulator: (A, R) -> R): R =
		foldMap(
			endoMonoid(),
			accumulator.curry()
		)(initialValue)

	/**
	 * Fold the structure by mapping to a monoidal value
	 */
	fun <M> foldMap(monoid: Monoid<M>, f: (A) -> M): M =
		foldL(monoid.empty, (monoid.dual()::combine compose f).flip())
}

fun <A> Foldable<A>.fold(monoid: Monoid<A>) = foldMap(monoid, id())
