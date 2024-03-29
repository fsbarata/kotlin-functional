package com.github.fsbarata.functional.data.maybe

import com.github.fsbarata.functional.assertEquals
import com.github.fsbarata.functional.data.Monoid
import com.github.fsbarata.functional.data.MonoidLaws
import com.github.fsbarata.functional.data.monoid.concatStringMonoid
import kotlin.test.Test

class OptionalMonoidTest: MonoidLaws<Optional<String>> {
	override val monoid: Monoid<Optional<String>> = Optional.monoid(concatStringMonoid())

	override val possibilities: Int = 3
	override fun factory(possibility: Int) =
		if (possibility == 0) Optional.empty()
		else Optional.just("$possibility")

	@Test
	fun concats() {
		with(Optional.monoid(concatStringMonoid())) {
			assertEquals(
				Optional.empty<String>(),
				concat(Optional.empty(), Optional.empty()),
			)

			assertEquals(
				Optional.just("5"),
				concat(Optional.just("5"), Optional.empty()),
			)

			assertEquals(
				Optional.just("2"),
				concat(Optional.empty(), Optional.just("2")),
			)

			assertEquals(
				Optional.just("42"),
				concat(Optional.just("4"), Optional.just("2")),
			)

			assertEquals(
				Optional.just("42"),
				Optional.just("4").concatWith(Optional.just("2")),
			)
		}
	}
}