package com.github.fsbarata.functional.rx.data

import com.github.fsbarata.functional.control.Functor
import com.github.fsbarata.functional.control.Monad
import com.github.fsbarata.functional.control.test.MonadTest
import com.github.fsbarata.functional.control.test.MonadZipTest

class MaybeFTest: MonadTest<MaybeF<*>>, MonadZipTest<MaybeF<*>> {
	override val monadScope = MaybeF
	override fun <A> Functor<MaybeF<*>, A>.equalTo(other: Functor<MaybeF<*>, A>): Boolean {
		val testObserver1 = asMaybe.materialize().test()
		val testObserver2 = other.asMaybe.materialize().test()

		return testObserver2.values() == testObserver1.values()
	}
}
