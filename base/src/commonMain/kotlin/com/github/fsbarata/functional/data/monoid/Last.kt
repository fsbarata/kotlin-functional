package com.github.fsbarata.functional.data.monoid

import com.github.fsbarata.functional.data.Monoid
import com.github.fsbarata.functional.data.Semigroup
import com.github.fsbarata.functional.data.monoid

data class Last<A: Any>(val get: A): Semigroup<Last<A>> {
	override fun concatWith(other: Last<A>) = Last(other.get)
}

data class LastNotNull<A>(val get: A): Semigroup<LastNotNull<A>> {
	override fun concatWith(other: LastNotNull<A>) = LastNotNull(other.get ?: get)

	companion object {
		fun <A: Any> monoid(): Monoid<LastNotNull<A?>> = monoid(LastNotNull(null))
	}
}

fun <A> lastNotNullMonoid() = object: Monoid<A?> {
	override val empty: A? = null

	override fun combine(a1: A?, a2: A?): A? = a2 ?: a1
}
