package com.fsbarata.fp.types

import com.fsbarata.fp.concepts.Monad
import org.junit.Assert.assertEquals
import org.junit.Test

class MonadExperimentKtTest {
	private fun <F: Monad<F, *>> Monad<F, Int>.multiply(
		x: Int,
	): Monad<F, Int> =
		if (x == 0) just(0)
		else bind { just(x * it) }

	@Test
	fun `multiply accepts List`() {
		assertEquals(
			listOf(15, 25),
			listOf(3, 5).f()
				.multiply(5)
				.asList
		)
		assertEquals(
			listOf(0),
			listOf(3, 5).f()
				.multiply(0)
				.asList
		)
	}

	@Test
	fun `multiply accepts Option`() {
		assertEquals(
			Optional.just(25),
			Optional.just(5)
				.multiply(5)
		)
		assertEquals(
			Optional.just(0),
			Optional.just(5)
				.multiply(0)
		)
		assertEquals(
			Optional.empty<Int>(),
			Optional.empty<Int>()
				.multiply(5)
		)
	}
}