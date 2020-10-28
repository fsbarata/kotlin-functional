package com.fsbarata.fp.types

import com.fsbarata.fp.concepts.Monad
import com.fsbarata.fp.concepts.test.MonadTest
import com.fsbarata.fp.concepts.test.MonadZipTest

class SingleFTest: MonadTest<SingleF<*>>, MonadZipTest<SingleF<*>> {
	override val monadScope = SingleF
	override fun Monad<SingleF<*>, Int>.equalTo(other: Monad<SingleF<*>, Int>): Boolean {
		val testObserver1 = asSingle.materialize().test()
		val testObserver2 = other.asSingle.materialize().test()

		return testObserver2.values() == testObserver1.values()
	}
}
