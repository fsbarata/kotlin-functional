package com.github.fsbarata.functional.data.monoid

import com.github.fsbarata.functional.data.Semigroup
import com.github.fsbarata.functional.data.monoid

class Last<A: Semigroup<A>>(val get: A?): Semigroup<Last<A>> {
	override fun combineWith(other: Last<A>) = Last(other.get ?: get)
}

fun <A: Semigroup<A>> lastMonoid() = monoid(Last<A>(null))
