package com.github.fsbarata.functional.samples.state

import com.github.fsbarata.functional.data.tuple.Tuple2
import com.github.fsbarata.functional.samples.state.randomGenerator
import org.junit.Assert.assertEquals
import org.junit.Test

class RandomGeneratorKtTest {
	@Test
	fun randomGen() {
		val seed = 200L
		val random = randomGenerator()
		val (seed2, out) = random.runState(seed)
		val (_, out2) = random.runState(seed2)

		assertEquals(Tuple2(seed2, out), random.runState(seed))
		assert(out != out2)
	}
}