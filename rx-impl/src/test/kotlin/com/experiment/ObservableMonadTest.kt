package com.experiment

import com.fsbarata.fp.concepts.Monad
import com.fsbarata.fp.concepts.test.MonadTest
import com.fsbarata.fp.types.ObservableF
import com.fsbarata.fp.types.asObservable

class ObservableMonadTest: MonadTest<ObservableF<*>>(ObservableF) {
	override fun Monad<ObservableF<*>, Int>.equalTo(other: Monad<ObservableF<*>, Int>): Boolean {
		val testObserver1 = asObservable.test()
		val testObserver2 = other.asObservable.test()

		return testObserver2.values() == testObserver1.values()
	}
}
