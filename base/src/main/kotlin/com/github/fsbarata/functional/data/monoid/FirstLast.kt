package com.github.fsbarata.functional.data.monoid

import com.github.fsbarata.functional.data.Monoid
import com.github.fsbarata.functional.data.Semigroup
import com.github.fsbarata.functional.data.monoid

class First<A>(val get: A?): Semigroup<First<A>> {
	override fun combineWith(other: First<A>) = First(get ?: other.get)

	companion object {
		fun <A> monoid() = monoid(First<A>(null))
	}
}

fun <A> firstMonoid() = object: Monoid<A?> {
	override val empty: A? = null

	override fun combine(a1: A?, a2: A?): A? = a1 ?: a2
}

class Last<A>(val get: A?): Semigroup<Last<A>> {
	override fun combineWith(other: Last<A>) = Last(other.get ?: get)

	companion object {
		fun <A> monoid() = monoid(Last<A>(null))
	}
}

fun <A> lastMonoid() = object: Monoid<A?> {
	override val empty: A? = null

	override fun combine(a1: A?, a2: A?): A? = a2 ?: a1
}
