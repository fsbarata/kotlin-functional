package com.github.fsbarata.functional.data.monoid

import com.github.fsbarata.functional.data.Monoid
import com.github.fsbarata.functional.data.Semigroup
import com.github.fsbarata.functional.data.monoid

class Dual<A: Semigroup<A>>(val get: A): Semigroup<Dual<A>> {
	override fun concatWith(other: Dual<A>) = Dual(other.get.concatWith(get))
}

fun <A: Semigroup<A>> A.dual() = Dual(this)

fun <A: Semigroup<A>> Monoid<A>.dual() = monoid(Dual(empty))
