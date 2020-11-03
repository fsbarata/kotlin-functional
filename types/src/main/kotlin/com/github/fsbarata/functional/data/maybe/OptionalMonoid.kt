package com.github.fsbarata.functional.data.maybe

import com.github.fsbarata.functional.data.Monoid
import com.github.fsbarata.functional.data.Semigroup


fun <A> optionalMonoid(sg: Semigroup<A>) = object: Monoid<Optional<A>> {
	override val empty = Optional.empty<A>()
	override fun combine(a1: Optional<A>, a2: Optional<A>): Optional<A> =
		a1.map { a -> a2.map { otherA -> sg.combine(a, otherA) } orElse a } orOptional a2
}
