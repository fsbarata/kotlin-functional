package com.github.fsbarata.functional.rx.data

import com.github.fsbarata.functional.control.Functor
import com.github.fsbarata.functional.control.test.MonadLaws
import com.github.fsbarata.functional.control.test.MonadZipLaws

class MaybeFTest: MonadLaws<MaybeF<*>>, MonadZipLaws<MaybeF<*>> {
	override val monadScope = MaybeF
	override fun <A> Functor<MaybeF<*>, A>.equalTo(other: Functor<MaybeF<*>, A>): Boolean {
		val testObserver1 = asMaybe.materialize().test()
		val testObserver2 = other.asMaybe.materialize().test()

		return testObserver2.values() == testObserver1.values()
	}
}
