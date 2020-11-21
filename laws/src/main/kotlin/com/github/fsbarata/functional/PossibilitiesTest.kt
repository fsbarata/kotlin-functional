package com.github.fsbarata.functional

interface PossibilitiesTest<A> {
	val possibilities: Int get() = 1
	fun factory(possibility: Int): A

	fun <T> eachPossibility(block: (A) -> T) =
		(0 until possibilities).map { block(factory(it)) }

}