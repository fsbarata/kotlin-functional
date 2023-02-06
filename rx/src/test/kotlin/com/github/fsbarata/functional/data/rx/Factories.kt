package com.github.fsbarata.functional.data.rx

import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.TestScheduler
import org.junit.Assert
import java.util.concurrent.TimeUnit

internal val error = Throwable()

internal fun completableFactory(possibility: Int) = when (possibility) {
	0 -> Completable.complete()
	1 -> Completable.never()
	else -> Completable.error(error)
}

internal fun maybeFactory(possibility: Int) = when (possibility) {
	0 -> MaybeF.empty()
	1 -> MaybeF.just(1)
	2 -> Maybe.error<Int>(error).f()
	else -> Completable.timer(10L * possibility, TimeUnit.MILLISECONDS)
		.andThen(Maybe.just(possibility))
		.f()
}

private val scheduler = TestScheduler()

internal fun observableFactory(possibility: Int): ObservableF<Int> = when (possibility) {
	0 -> ObservableF.empty()
	1 -> Observable.never<Int>().f()
	2 -> Observable.error<Int>(error).f()
	3 -> ObservableF.just(0)
	else -> Observable.just(possibility)
		.delay((possibility.toLong() - 3) * 10L, TimeUnit.MILLISECONDS, scheduler)
		.concatWith(observableFactory(possibility - 3))
		.f()
}

internal fun <A: Any> assertEqualObs(a1: ObservableF<A>, a2: ObservableF<A>) {
	val testObserver1 = a1.materialize().test()
	val testObserver2 = a2.materialize().test()

	scheduler.advanceTimeBy(10, TimeUnit.SECONDS)

	return Assert.assertEquals(testObserver2.values(), testObserver1.values())
}
