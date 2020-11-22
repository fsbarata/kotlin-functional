package com.github.fsbarata.functional.data

interface Invariant<F, out A> {
	fun <B> invmap(f: (A) -> B, g: (B) -> @UnsafeVariance A): Invariant<F, B>
}

class InvariantFunctor<F, A, T: Functor<F, A>>(val get: T): Invariant<F, A> {
	override fun <B> invmap(f: (A) -> B, g: (B) -> A) = InvariantFunctor(get.map(f))
}

class InvariantContravariant<F, A, T: Contravariant<F, A>>(val get: T): Invariant<F, A> {
	override fun <B> invmap(f: (A) -> B, g: (B) -> A) = InvariantContravariant(get.contramap(g))
}
