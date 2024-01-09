package com.github.fsbarata.functional.data.monoid

import com.github.fsbarata.functional.data.Monoid
import com.github.fsbarata.functional.data.Semigroup
import com.github.fsbarata.functional.data.monoid
import com.github.fsbarata.functional.data.monoidOf

fun <A> firstSemigroupScope(): Semigroup.Scope<A> = Semigroup.Scope { a1, _ -> a1 }
fun <A> lastSemigroupScope(): Semigroup.Scope<A> = Semigroup.Scope { _, a2 -> a2 }

data class FirstNotNull<A: Any>(val get: A?): Semigroup<FirstNotNull<A>> {
	override fun concatWith(other: FirstNotNull<A>) = FirstNotNull(get ?: other.get)

	companion object {
		fun <A: Any> monoid(): Monoid<FirstNotNull<A>> = monoid(FirstNotNull(null))
	}
}

fun <A: Any> firstNotNullMonoid() = object: Monoid<A?> {
	override val empty: A? = null

	override fun concat(a1: A?, a2: A?): A? = a1 ?: a2
}

data class LastNotNull<A>(val get: A?): Semigroup<LastNotNull<A>> {
	override fun concatWith(other: LastNotNull<A>) = LastNotNull(other.get ?: get)

	companion object {
		fun <A: Any> monoid(): Monoid<LastNotNull<A>> = monoid(LastNotNull(null))
	}
}

fun <A: Any> lastNotNullMonoid() = object: Monoid<A?> {
	override val empty: A? = null

	override fun concat(a1: A?, a2: A?): A? = a2 ?: a1
}

fun allMonoid() = monoidOf(true, Boolean::and)
fun anyMonoid() = monoidOf(false, Boolean::or)

fun <A: Comparable<A>> maxSemigroupScope(): Semigroup.Scope<A> = Semigroup.Scope<A>(::maxOf)
fun <A> maxSemigroupScope(comparator: Comparator<A>) =
	Semigroup.Scope<A> { a, b -> maxOf(a, b, comparator) }
fun <A: Comparable<A>> minSemigroupScope(): Semigroup.Scope<A> = Semigroup.Scope<A>(::minOf)
fun <A> minSemigroupScope(comparator: Comparator<A>) =
	Semigroup.Scope<A> { a, b -> minOf(a, b, comparator) }

