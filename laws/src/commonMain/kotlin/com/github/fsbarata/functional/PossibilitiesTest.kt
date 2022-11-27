package com.github.fsbarata.functional

interface PossibilitiesTest {
	val possibilities: Int get() = 1
	fun factory(possibility: Int): Any?

	fun <T> eachPossibility(block: (Any?) -> T) =
		(0 until possibilities).map { block(factory(it)) }

	fun requestOne(): Any? {
		(0 until possibilities).map { return factory(it) }
		throw NoSuchElementException()
	}
}