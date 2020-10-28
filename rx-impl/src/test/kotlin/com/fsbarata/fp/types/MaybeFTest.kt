package com.fsbarata.fp.types

import com.fsbarata.fp.concepts.Monad
import com.fsbarata.fp.concepts.test.MonadTest
import com.fsbarata.fp.concepts.test.MonadZipTest

class MaybeFTest: MonadTest<MaybeF<*>>, MonadZipTest<MaybeF<*>> {
	override val monadScope = MaybeF
	override fun Monad<MaybeF<*>, Int>.equalTo(other: Monad<MaybeF<*>, Int>): Boolean {
		val testObserver1 = asMaybe.materialize().test()
		val testObserver2 = other.asMaybe.materialize().test()

		return testObserver2.values() == testObserver1.values()
	}
}
