package com.github.fsbarata.functional.rx.data

import com.github.fsbarata.functional.control.Monad
import com.github.fsbarata.functional.control.test.MonadTest
import com.github.fsbarata.functional.control.test.MonadZipTest

class FlowableFTest: MonadTest<FlowableF<*>>, MonadZipTest<FlowableF<*>> {
	override val monadScope = FlowableF
	override fun Monad<FlowableF<*>, Int>.equalTo(other: Monad<FlowableF<*>, Int>): Boolean {
		val testObserver1 = asFlowable.materialize().test()
		val testObserver2 = other.asFlowable.materialize().test()

		return testObserver2.values() == testObserver1.values()
	}
}
