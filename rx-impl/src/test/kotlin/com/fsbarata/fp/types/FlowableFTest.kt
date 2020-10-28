package com.fsbarata.fp.types

import com.fsbarata.fp.concepts.Monad
import com.fsbarata.fp.concepts.test.MonadTest
import com.fsbarata.fp.concepts.test.MonadZipTest

class FlowableFTest: MonadTest<FlowableF<*>>, MonadZipTest<FlowableF<*>> {
	override val monadScope = FlowableF
	override fun Monad<FlowableF<*>, Int>.equalTo(other: Monad<FlowableF<*>, Int>): Boolean {
		val testObserver1 = asFlowable.materialize().test()
		val testObserver2 = other.asFlowable.materialize().test()

		return testObserver2.values() == testObserver1.values()
	}
}
