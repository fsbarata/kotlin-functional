package com.github.fsbarata.functional.data.maybe

import com.github.fsbarata.functional.data.monoid.productIntMonoid
import com.github.fsbarata.functional.data.test.MonoidLaws
import org.junit.Test

class OptionalSumMonoidTest: MonoidLaws<Optional<Int>>(
	Optional.monoid(productIntMonoid()),
) {
	override val possibilities: Int = 100
	override fun nonEmpty(possibility: Int) = Optional.just(possibility)

	@Test
	fun combines() {
		with(Optional.monoid(productIntMonoid())) {
			assertEquals(
				Optional.just(5),
				combine(Optional.just(5), Optional.empty())
			)

			assertEquals(
				Optional.just(2),
				combine(Optional.empty(), Optional.just(2))
			)

			assertEquals(
				Optional.just(4),
				combine(Optional.just(2), Optional.just(2))
			)
		}
	}
}