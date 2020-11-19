package com.github.fsbarata.functional.rx.data

import com.github.fsbarata.functional.control.test.MonadZipLaws
import com.github.fsbarata.functional.data.Functor

class SingleFTest: MonadZipLaws<SingleF<*>> {
	override val monadScope = SingleF
	override fun <A> Functor<SingleF<*>, A>.equalTo(other: Functor<SingleF<*>, A>): Boolean {
		val testObserver1 = asSingle.materialize().test()
		val testObserver2 = other.asSingle.materialize().test()

		return testObserver2.values() == testObserver1.values()
	}
}
