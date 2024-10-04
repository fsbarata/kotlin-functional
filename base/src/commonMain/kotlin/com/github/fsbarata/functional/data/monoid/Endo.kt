package com.github.fsbarata.functional.data.monoid

import com.github.fsbarata.functional.data.*

typealias Endomorphism<A> = F1<A, A>

fun <A> endoSemigroup() = Semigroup.Scope<Endomorphism<A>>(::compose)
fun <A> endoMonoid(): Monoid<Endomorphism<A>> = monoidOf(::id, ::compose)

inline fun <A> idEndo(): Endomorphism<A> = ::id

/**
 * Apply an endomorphism n times
 * Equivalent to generateSequence(a, f).take(n).last()
 */
inline fun <A> fapplyN(a: A, n: Int, f: Endomorphism<A>): A {
	return (0 until n).fold(a) { r, _ -> f(r) }
}

