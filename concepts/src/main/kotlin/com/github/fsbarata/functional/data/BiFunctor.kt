package com.github.fsbarata.functional.data

interface BiFunctor<F, out B, out A> {
	fun <C, D> bimap(f: (B) -> C, g: (A) -> D): BiFunctor<F, C, D> =
		mapLeft(f).map(g)

	fun <C> mapLeft(f: (B) -> C): BiFunctor<F, C, A> =
		bimap(f, id())

	fun <C> map(f: (A) -> C): BiFunctor<F, B, C> =
		bimap(id(), f)
}
