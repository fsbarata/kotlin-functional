package com.experiment

import com.fsbarata.fp.concepts.Monad
import com.fsbarata.fp.types.Option
import com.fsbarata.fp.types.f
import com.fsbarata.fp.types.asList
import org.junit.Assert.assertEquals
import org.junit.Test

class MonadExperimentKtTest {
	private fun <F : Any> Monad<F, Int>.multiply(
			x: Int
	): Monad<F, Int> =
			if (x == 0) just(0)
			else flatMap { just(x * it) }

	@Test
	fun `multiply accepts List`() {
		assertEquals(listOf(15, 25),
				listOf(3, 5).f()
						.multiply(5)
						.asList
		)
		assertEquals(listOf(0),
				listOf(3, 5).f()
						.multiply(0)
						.asList
		)
	}

	@Test
	fun `multiply accepts Option`() {
		assertEquals(Option.just(25),
				Option.just(5)
						.multiply(5)
		)
		assertEquals(Option.just(0),
				Option.just(5)
						.multiply(0)
		)
		assertEquals(Option.empty<Int>(),
				Option.empty<Int>()
						.multiply(5)
		)
	}
}