package com.github.fsbarata.functional.rx.data

import com.github.fsbarata.functional.control.MonadZipLaws
import com.github.fsbarata.functional.data.Functor
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.core.Single
import java.io.IOException
import java.util.concurrent.TimeUnit

class MaybeFTest: MonadZipLaws<MaybeF<*>> {
	override val monadScope = MaybeF
	override fun <A> Functor<MaybeF<*>, A>.equalTo(other: Functor<MaybeF<*>, A>): Boolean {
		val testObserver1 = asMaybe.materialize().test()
		val testObserver2 = other.asMaybe.materialize().test()

		return testObserver2.values() == testObserver1.values()
	}

	val error = IOException()
	override val possibilities: Int = 4
	override fun factory(possibility: Int) = when (possibility) {
		0 -> MaybeF.empty()
		1 -> MaybeF.just(1)
		2 -> Maybe.error<Int>(error).f()
		else -> Completable.timer(10L * possibility, TimeUnit.MILLISECONDS)
			.andThen(Maybe.just(possibility))
			.f()
	}
}
