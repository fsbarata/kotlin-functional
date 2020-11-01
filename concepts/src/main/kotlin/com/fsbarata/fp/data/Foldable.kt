package com.fsbarata.fp.data

interface Foldable<out A> {
	fun <R> foldL(initialValue: R, accumulator: (R, A) -> R): R =
		foldMap(
			endoMonoid<R>().dual(),
			(accumulator.flip()::partial)
		)(initialValue)

	fun <R> foldR(initialValue: R, accumulator: (A, R) -> R): R =
		foldMap(endoMonoid(), accumulator::partial)(initialValue)

	fun <M> foldMap(monoid: Monoid<M>, f: (A) -> M): M =
		foldR(monoid.empty, (f composeForward monoid::combine.curry()).uncurry())
}

fun <A> Foldable<A>.fold(monoid: Monoid<A>) = foldMap(monoid, id())
