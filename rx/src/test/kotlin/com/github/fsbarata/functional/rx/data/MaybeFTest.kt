package com.github.fsbarata.functional.rx.data

import com.github.fsbarata.functional.control.MonadZipLaws
import com.github.fsbarata.functional.data.Functor
import com.github.fsbarata.functional.rx.maybeFactory

class MaybeFTest: MonadZipLaws<MaybeContext> {
	override val monadScope = MaybeF
	override fun <A> Functor<MaybeContext, A>.equalTo(other: Functor<MaybeContext, A>): Boolean {
		val testObserver1 = asMaybe.materialize().test()
		val testObserver2 = other.asMaybe.materialize().test()

		return testObserver2.values() == testObserver1.values()
	}

	override val possibilities: Int = 4
	override fun factory(possibility: Int) = maybeFactory(possibility)
}
