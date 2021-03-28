package com.github.fsbarata.functional.data.rx

import com.github.fsbarata.functional.Context
import com.github.fsbarata.functional.control.MonadZipLaws
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import java.io.IOException
import java.util.concurrent.TimeUnit

class SingleFTest: MonadZipLaws<SingleContext> {
	override val monadScope = SingleF
	override fun <A> Context<SingleContext, A>.equalTo(other: Context<SingleContext, A>): Boolean {
		val testObserver1 = asSingle.materialize().test()
		val testObserver2 = other.asSingle.materialize().test()

		return testObserver2.values() == testObserver1.values()
	}

	val error = IOException()
	override val possibilities: Int = 3
	override fun factory(possibility: Int) = when (possibility) {
		0 -> SingleF.just(1)
		1 -> Single.error<Int>(error).f()
		else -> Completable.timer(10L * possibility, TimeUnit.MILLISECONDS)
			.andThen(Single.just(possibility))
			.f()
	}
}
