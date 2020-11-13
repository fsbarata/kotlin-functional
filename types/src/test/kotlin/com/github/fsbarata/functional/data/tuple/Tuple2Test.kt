package com.github.fsbarata.functional.data.tuple

import com.github.fsbarata.functional.control.test.ComonadLaws
import com.github.fsbarata.functional.data.test.BiFunctorLaws
import com.github.fsbarata.functional.data.test.TraversableLaws

class Tuple2Test:
	TraversableLaws<Tuple2Context<Double>>,
	BiFunctorLaws<Tuple2BiContext>,
	ComonadLaws<Tuple2Context<Double>> {

	override fun <B, A> createBiFunctor(a: A, b: B) = Tuple2(b, a)

	override val traversableScope = Tuple2.Scope<Double>()

	override fun <A> createTraversable(vararg items: A) = createFunctor(items.first())
	override fun <A> createFunctor(a: A) = Tuple2(2.0, a)
}
