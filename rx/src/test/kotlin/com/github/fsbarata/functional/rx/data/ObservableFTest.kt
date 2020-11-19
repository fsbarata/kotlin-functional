package com.github.fsbarata.functional.rx.data

import com.github.fsbarata.functional.control.test.MonadLaws
import com.github.fsbarata.functional.control.test.MonadZipLaws
import com.github.fsbarata.functional.data.Functor
import com.github.fsbarata.functional.data.test.SemigroupLaws
import com.github.fsbarata.functional.rx.observableFactory
import org.junit.Assert.assertEquals

class ObservableFTest:
	MonadLaws<ObservableF<*>>,
	MonadZipLaws<ObservableF<*>>,
	SemigroupLaws<ObservableF<Int>> {
	override val monadScope = ObservableF

	override val possibilities: Int = 10
	override fun factory(possibility: Int): ObservableF<Int> = observableFactory(possibility)

	override fun <A> Functor<ObservableF<*>, A>.equalTo(other: Functor<ObservableF<*>, A>): Boolean {
		val testObserver1 = asObservable.materialize().test()
		val testObserver2 = other.asObservable.materialize().test()

		return testObserver2.values() == testObserver1.values()
	}

	override fun <A> assertEqualF(r1: Functor<ObservableF<*>, A>, r2: Functor<ObservableF<*>, A>) {
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
