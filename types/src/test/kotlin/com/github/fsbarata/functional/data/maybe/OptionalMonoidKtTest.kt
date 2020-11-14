package com.github.fsbarata.functional.data.maybe

import com.github.fsbarata.functional.data.string.StringF
import com.github.fsbarata.functional.data.test.MonoidLaws
import org.junit.Test

class OptionalSumMonoidTest: MonoidLaws<Optional<StringF>>(
	Optional.monoid(),
) {
	override val possibilities: Int = 100
	override fun nonEmpty(possibility: Int) = Optional.just(StringF("$possibility"))

	@Test
	fun combines() {
		with(Optional.monoid<StringF>()) {
			assertEquals(
				Optional.just(StringF("5")),
				combine(Optional.just(StringF("5")), Optional.empty())
			)

			assertEquals(
				Optional.just(StringF("2")),
				combine(Optional.empty(), Optional.just(StringF("2")))
			)

			assertEquals(
				Optional.just(StringF("42")),
				combine(Optional.just(StringF("4")), Optional.just(StringF("2")))
			)
		}
	}
}