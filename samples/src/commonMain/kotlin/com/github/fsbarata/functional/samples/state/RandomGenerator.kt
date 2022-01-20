package com.github.fsbarata.functional.samples.state

import com.github.fsbarata.functional.control.state.State
import com.github.fsbarata.functional.data.tuple.Tuple2

/**
 * Generates random numbers from 0 to 20.
 * Does not store any state, see how to use it in RandomGeneratorKtTest
 */
fun randomGenerator() = State { seed: Long ->
	val newSeed = 75L * (seed + 12)
	Tuple2(newSeed, (newSeed % 21).toInt())
}

