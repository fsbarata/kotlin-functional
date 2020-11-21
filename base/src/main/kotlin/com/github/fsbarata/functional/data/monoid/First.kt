package com.github.fsbarata.functional.data.monoid

import com.github.fsbarata.functional.data.Semigroup
import com.github.fsbarata.functional.data.monoid

class First<A: Semigroup<A>>(val get: A?): Semigroup<First<A>> {
	override fun combineWith(other: First<A>) = First(get ?: other.get)
}

fun <A: Semigroup<A>> firstMonoid() = monoid(First<A>(null))
