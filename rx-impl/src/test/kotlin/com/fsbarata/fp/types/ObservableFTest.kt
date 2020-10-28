package com.fsbarata.fp.types

import com.fsbarata.fp.concepts.Monad
import com.fsbarata.fp.concepts.test.MonadTest

class ObservableFTest: MonadTest<ObservableF<*>> {
	override val monadScope = ObservableF
	override fun Monad<ObservableF<*>, Int>.equalTo(other: Monad<ObservableF<*>, Int>): Boolean {
		val testObserver1 = asObservable.materialize().test()
		val testObserver2 = other.asObservable.materialize().test()

		return testObserver2.values() == testObserver1.values()
	}
}
