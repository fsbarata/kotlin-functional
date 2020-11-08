package com.github.fsbarata.functional.data.tuple

import com.github.fsbarata.functional.data.test.TraversableTest

class Tuple2Test: TraversableTest<Tuple2Context<Double>> {
	override val traversableScope = Tuple2.Scope<Double>()

	override fun <A> createTraversable(vararg items: A) =
		Tuple2(2.0, items.first())
}