package com.github.fsbarata.functional.data.maybe

import com.github.fsbarata.functional.data.Monoid
import com.github.fsbarata.functional.data.Semigroup


fun <A> optionalMonoid(sg: Semigroup<A>) = object: Monoid<Optional<A>> {
	override val empty = Optional.empty<A>()
	override fun combine(opt1: Optional<A>, opt2: Optional<A>): Optional<A> =
		opt1.map { a -> opt2.map { otherA -> sg.combine(a, otherA) } orElse a } orOptional opt2
}
