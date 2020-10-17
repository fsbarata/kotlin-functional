package com.fsbarata.fp.types

import com.fsbarata.fp.concepts.Monad
import com.fsbarata.fp.concepts.test.MonadTest
import com.fsbarata.fp.types.ObservableF
import com.fsbarata.fp.types.asObservable

class ObservableFTest: MonadTest<ObservableF<*>>(ObservableF) {
	override fun Monad<ObservableF<*>, Int>.equalTo(other: Monad<ObservableF<*>, Int>): Boolean {
		val testObserver1 = asObservable.materialize().test()
		val testObserver2 = other.asObservable.materialize().test()

		return testObserver2.values() == testObserver1.values()
	}
}
