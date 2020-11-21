package com.github.fsbarata.functional.rx.data

import com.github.fsbarata.functional.control.MonadZipLaws
import com.github.fsbarata.functional.data.Functor
import com.github.fsbarata.functional.rx.observableFactory
import io.reactivex.rxjava3.core.BackpressureStrategy

class FlowableFTest: MonadZipLaws<FlowableF<*>> {
	override val monadScope = FlowableF
	override fun <A> Functor<FlowableF<*>, A>.equalTo(other: Functor<FlowableF<*>, A>): Boolean {
		val testObserver1 = asFlowable.materialize().test()
		val testObserver2 = other.asFlowable.materialize().test()

		return testObserver2.values() == testObserver1.values()
	}

	override val possibilities: Int = 5
	override fun factory(possibility: Int) =
		observableFactory(possibility).toFlowable(BackpressureStrategy.BUFFER).f()
}
