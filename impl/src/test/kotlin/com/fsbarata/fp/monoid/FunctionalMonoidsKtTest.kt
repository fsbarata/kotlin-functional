package com.fsbarata.fp.monoid

import com.fsbarata.fp.concepts.test.MonoidTest
import com.fsbarata.fp.types.Optional
import org.junit.Assert.assertEquals
import org.junit.Test
import kotlin.random.Random

class OptionalSumMonoidTest: MonoidTest<Optional<Int>>(
	optionalMonoid(productIntMonoid()),
	{ Optional.just(Random.nextInt(1, 100)) }
) {
	@Test
	fun combines() {
		with(optionalMonoid(productIntMonoid())) {
			assertEquals(
				Optional.just(5),
				combine(Optional.just(5), Optional.empty())
			)

			assertEquals(
				Optional.just(2),
				combine(Optional.empty<Int>(), Optional.just(2))
			)

			assertEquals(
				Optional.just(4),
				combine(Optional.just(2), Optional.just(2))
			)
		}
	}
}