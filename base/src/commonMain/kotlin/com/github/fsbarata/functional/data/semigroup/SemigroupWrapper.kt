package com.github.fsbarata.functional.data.semigroup

import com.github.fsbarata.functional.data.Invariant
import com.github.fsbarata.functional.data.Semigroup
import com.github.fsbarata.functional.data.combine

abstract class SemigroupWrapper<A>(val get: A): Semigroup<SemigroupWrapper<A>>, Invariant<Semigroup<*>, A> {
	abstract fun combine(a1: A, a2: A): A
	override fun combineWith(other: SemigroupWrapper<A>): SemigroupWrapper<A> =
		copy(combine(get, other.get))

	fun copy(a: A): SemigroupWrapper<A> = object: SemigroupWrapper<A>(a) {
		override fun combine(a1: A, a2: A): A = this@SemigroupWrapper.combine(a1, a2)
	}

	override fun <B> invmap(f: (A) -> B, g: (B) -> A): SemigroupWrapper<B> {
		return wrapInSemigroup(f(get), combine = { b1, b2 -> f(combine(g(b1), g(b2))) })
	}
}

fun <A> wrapInSemigroup(a: A, combine: (A, A) -> A): SemigroupWrapper<A> = object: SemigroupWrapper<A>(a) {
	override fun combine(a1: A, a2: A): A = combine(a1, a2)
}

fun <A: Semigroup<A>> A.toInvariant(): Invariant<Semigroup<*>, A> =
	wrapInSemigroup(this, ::combine)
