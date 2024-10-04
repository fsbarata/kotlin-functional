package com.github.fsbarata.functional.data.monoid

import com.github.fsbarata.functional.data.*

typealias Endomorphism<A> = F1<A, A>

fun <A> endoSemigroup() = Semigroup.Scope<Endomorphism<A>>(::compose)
fun <A> endoMonoid(): Monoid<Endomorphism<A>> = monoidOf(::id, ::compose)
