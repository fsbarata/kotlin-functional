package com.github.fsbarata.functional.data

interface Contravariant<F, A>: Invariant<F, A> {
	fun <B> contramap(f: (B) -> A): Contravariant<F, B>

	override fun <B> invmap(f: (A) -> B, g: (B) -> A) = contramap(g)
}

