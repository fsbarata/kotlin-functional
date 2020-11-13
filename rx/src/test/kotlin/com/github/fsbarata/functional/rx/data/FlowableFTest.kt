package com.github.fsbarata.functional.rx.data

import com.github.fsbarata.functional.control.test.MonadLaws
import com.github.fsbarata.functional.control.test.MonadZipLaws
import com.github.fsbarata.functional.data.Functor

class FlowableFTest: MonadLaws<FlowableF<*>>, MonadZipLaws<FlowableF<*>> {
	override val monadScope = FlowableF
	override fun <A> Functor<FlowableF<*>, A>.equalTo(other: Functor<FlowableF<*>, A>): Boolean {
		val testObserver1 = asFlowable.materialize().test()
		val testObserver2 = other.asFlowable.materialize().test()

		return testObserver2.values() == testObserver1.values()
	}
}
