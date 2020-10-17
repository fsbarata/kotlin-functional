package com.fsbarata.fp.monoid

import com.fsbarata.fp.concepts.Monoid
import com.fsbarata.fp.concepts.Semigroup
import com.fsbarata.fp.types.Optional
import com.fsbarata.fp.types.orElse
import com.fsbarata.fp.types.orOptional


fun <A> optionalMonoid(sg: Semigroup<A>) = object: Monoid<Optional<A>> {
	override val empty = Optional.empty<A>()
	override fun Optional<A>.combine(other: Optional<A>): Optional<A> =
		map { a -> other.map { otherA -> with(sg) { a.combine(otherA) } } orElse a } orOptional other
}
