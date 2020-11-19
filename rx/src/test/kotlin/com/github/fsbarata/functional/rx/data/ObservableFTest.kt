package com.github.fsbarata.functional.rx.data

import com.github.fsbarata.functional.control.test.MonadLaws
import com.github.fsbarata.functional.control.test.MonadZipLaws
import com.github.fsbarata.functional.data.Functor
import com.github.fsbarata.functional.data.test.SemigroupLaws
import com.github.fsbarata.functional.rx.observableFactory

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
}
