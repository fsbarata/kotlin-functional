package com.github.fsbarata.functional.data.maybe

import com.github.fsbarata.functional.data.Monoid
import com.github.fsbarata.functional.data.Semigroup

class OptionalMonoid<A>(
	private val semigroupScope: Semigroup.Scope<A>,
): Monoid<Optional<A>> {
	override val empty = Optional.empty<A>()
	override fun concat(a1: Optional<A>, a2: Optional<A>): Optional<A> =
		a1.fold(
			ifEmpty = { a2 },
			ifSome = { a -> a2.map { semigroupScope.concat(a, it) } orOptional a1 }
		)
}

