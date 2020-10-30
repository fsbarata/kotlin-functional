package com.fsbarata.fp.monoid

import com.fsbarata.fp.concepts.Monoid
import com.fsbarata.fp.concepts.Semigroup
import com.fsbarata.fp.types.Optional
import com.fsbarata.fp.types.orElse
import com.fsbarata.fp.types.orOptional


fun <A> optionalMonoid(sg: Semigroup<A>) = object: Monoid<Optional<A>> {
	override val empty = Optional.empty<A>()
	override fun combine(opt1: Optional<A>, opt2: Optional<A>): Optional<A> =
		opt1.map { a -> opt2.map { otherA -> sg.combine(a, otherA) } orElse a } orOptional opt2
}
