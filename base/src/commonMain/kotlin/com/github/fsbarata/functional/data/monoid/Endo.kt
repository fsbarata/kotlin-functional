package com.github.fsbarata.functional.data.monoid

import com.github.fsbarata.functional.data.*

typealias Endomorphism<A> = F1<A, A>

fun <A> endoSemigroup() = Semigroup.Scope<Endomorphism<A>>(::compose)
fun <A> endoMonoid(): Monoid<Endomorphism<A>> = monoidOf(::id, ::compose)

/**
 * Apply an endomorphism n times
 * Equivalent to generateSequence(a, f).take(n).last()
 */
tailrec fun <A> fapplyN(a: A, n: Int, f: Endomorphism<A>): A =
	if (n <= 0) a
	else fapplyN(f(a), n - 1, f)
