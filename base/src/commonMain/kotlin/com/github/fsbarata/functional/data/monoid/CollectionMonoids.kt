package com.github.fsbarata.functional.data.monoid

import com.github.fsbarata.functional.data.Monoid
import com.github.fsbarata.functional.data.Semigroup
import com.github.fsbarata.functional.data.monoid
import com.github.fsbarata.functional.data.monoidOf


data class FirstNotNull<A: Any>(val get: A?): Semigroup<FirstNotNull<A>> {
	override fun concatWith(other: FirstNotNull<A>) = FirstNotNull(get ?: other.get)

	companion object {
		fun <A: Any> monoid(): Monoid<FirstNotNull<A>> = monoid(FirstNotNull(null))
	}
}

fun <A> firstNotNullMonoid() = object: Monoid<A?> {
	override val empty: A? = null

	override fun concat(a1: A?, a2: A?): A? = a1 ?: a2
}

data class LastNotNull<A>(val get: A?): Semigroup<LastNotNull<A>> {
	override fun concatWith(other: LastNotNull<A>) = LastNotNull(other.get ?: get)

	companion object {
		fun <A: Any> monoid(): Monoid<LastNotNull<A>> = monoid(LastNotNull(null))
	}
}

fun <A> lastNotNullMonoid() = object: Monoid<A?> {
	override val empty: A? = null

	override fun concat(a1: A?, a2: A?): A? = a2 ?: a1
}

fun allMonoid() = monoidOf(true, Boolean::and)
fun anyMonoid() = monoidOf(false, Boolean::or)

fun <A: Comparable<A>> maxSemigroupScope(): Semigroup.Scope<A> = Semigroup.Scope<A>(::maxOf)
fun <A: Comparable<A>> minSemigroupScope(): Semigroup.Scope<A> = Semigroup.Scope<A>(::minOf)

