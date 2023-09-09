package com.github.fsbarata.functional.data

import com.github.fsbarata.functional.BiContext

interface BiFunctor<P, out B, out A>: BiContext<P, B, A> {
	fun <C, D> bimap(f: (B) -> C, g: (A) -> D): BiFunctor<P, C, D> =
		mapLeft(f).map(g)

	fun <C> mapLeft(f: (B) -> C): BiFunctor<P, C, A> =
		bimap(f, ::id)

	fun <C> map(f: (A) -> C): BiFunctor<P, B, C> =
		bimap(::id, f)
}
