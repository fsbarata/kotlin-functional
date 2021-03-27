package com.github.fsbarata.functional.data.monoid

import com.github.fsbarata.functional.data.Monoid
import com.github.fsbarata.functional.data.Semigroup
import com.github.fsbarata.functional.data.monoid

data class First<A: Any>(val get: A): Semigroup<First<A>> {
	override fun combineWith(other: First<A>) = First(get)
}

data class FirstNotNull<A>(val get: A): Semigroup<FirstNotNull<A>> {
	override fun combineWith(other: FirstNotNull<A>) = FirstNotNull(get ?: other.get)

	companion object {
		fun <A: Any> monoid(): Monoid<FirstNotNull<A?>> = monoid(FirstNotNull(null))
	}
}

fun <A> firstMonoid() = object: Monoid<A?> {
	override val empty: A? = null

	override fun combine(a1: A?, a2: A?): A? = a1 ?: a2
}

data class Last<A: Any>(val get: A): Semigroup<Last<A>> {
	override fun combineWith(other: Last<A>) = Last(other.get)
}

data class LastNotNull<A>(val get: A): Semigroup<LastNotNull<A>> {
	override fun combineWith(other: LastNotNull<A>) = LastNotNull(other.get ?: get)

	companion object {
		fun <A: Any> monoid(): Monoid<LastNotNull<A?>> = monoid(LastNotNull(null))
	}
}

fun <A> lastMonoid() = object: Monoid<A?> {
	override val empty: A? = null

	override fun combine(a1: A?, a2: A?): A? = a2 ?: a1
}
