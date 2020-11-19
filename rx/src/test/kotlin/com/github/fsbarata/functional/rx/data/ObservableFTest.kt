package com.github.fsbarata.functional.rx.data

import com.github.fsbarata.functional.control.test.MonadPlusLaws
import com.github.fsbarata.functional.control.test.MonadZipLaws
import com.github.fsbarata.functional.data.Functor
import com.github.fsbarata.functional.data.test.SemigroupLaws
import com.github.fsbarata.functional.rx.observableFactory
import org.junit.Assert.assertEquals

class ObservableFTest:
	MonadPlusLaws<ObservableContext>,
	MonadZipLaws<ObservableContext>,
	SemigroupLaws<ObservableF<Int>> {
	override val monadScope = ObservableF

	override val possibilities: Int = 10
	override fun factory(possibility: Int): ObservableF<Int> = observableFactory(possibility)

	override fun <A> assertEqualF(r1: Functor<ObservableContext, A>, r2: Functor<ObservableContext, A>) {
		assertEqualObs(r1.asObservable, r2.asObservable)
	}

	override fun assertEqual(a1: ObservableF<Int>, a2: ObservableF<Int>) {
		assertEqualObs(a1, a2)
	}

	private fun <A> assertEqualObs(a1: ObservableF<A>, a2: ObservableF<A>) {
		val testObserver1 = a1.materialize().test()
		val testObserver2 = a2.materialize().test()

		return assertEquals(testObserver2.values(), testObserver1.values())
	}
}
