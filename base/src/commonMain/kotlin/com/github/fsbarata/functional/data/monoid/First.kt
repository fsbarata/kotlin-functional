package com.github.fsbarata.functional.data.monoid

import com.github.fsbarata.functional.data.Monoid
import com.github.fsbarata.functional.data.Semigroup
import com.github.fsbarata.functional.data.monoid

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
