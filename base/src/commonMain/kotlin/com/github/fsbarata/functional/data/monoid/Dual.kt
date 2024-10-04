package com.github.fsbarata.functional.data.monoid

import com.github.fsbarata.functional.data.*

class Dual<A: Semigroup<A>>(val get: A): Semigroup<Dual<A>> {
	override fun concatWith(other: Dual<A>) = Dual(other.get.concatWith(get))
}

fun <A: Semigroup<A>> A.dual() = Dual(this)
fun <A> Semigroup.Scope<A>.dual() = Semigroup.Scope(flip(::concat))

fun <A> Monoid<A>.dual(): Monoid<A> = monoidOf(empty, flip(::concat))
