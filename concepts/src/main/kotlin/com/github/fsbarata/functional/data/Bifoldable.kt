package com.github.fsbarata.functional.data

interface Bifoldable<A, B> {
	fun <R> bifoldL(initialValue: R, f: (R, A) -> R, g: (R, B) -> R): R =
		bifoldMap(
			endoMonoid<R>().dual(),
			f.flip().curry(),
			g.flip().curry(),
		)(initialValue)

	fun <R> bifoldR(initialValue: R, f: (A, R) -> R, g: (B, R) -> R): R =
		bifoldMap(
			endoMonoid(),
			f.curry(),
			g.curry(),
		)(initialValue)

	fun <M> bifoldMap(monoid: Monoid<M>, f: (A) -> M, g: (B) -> M): M =
		bifoldL(
			monoid.empty,
			(monoid.dual()::combine compose f).flip(),
			(monoid.dual()::combine compose g).flip()
		)
}

fun <M> Bifoldable<M, M>.fold(monoid: Monoid<M>) = bifoldMap(monoid, id(), id())
