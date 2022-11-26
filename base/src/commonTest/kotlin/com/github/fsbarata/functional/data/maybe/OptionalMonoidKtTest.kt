package com.github.fsbarata.functional.data.maybe

import com.github.fsbarata.functional.assertEquals
import com.github.fsbarata.functional.data.MonoidLaws
import com.github.fsbarata.functional.data.StringF
import com.github.fsbarata.functional.data.monoid.concatStringMonoid
import kotlin.test.Test

class OptionalMonoidTest: MonoidLaws<Optional<StringF>>(
	Optional.monoid(),
) {
	override val possibilities: Int = 100
	override fun factory(possibility: Int) = Optional.just(StringF("$possibility"))

	@Test
	fun combines() {
		with(Optional.monoid(concatStringMonoid())) {
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
		}
	}
}