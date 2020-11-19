package com.github.fsbarata.functional.rx

import com.github.fsbarata.functional.rx.data.ObservableF
import com.github.fsbarata.functional.rx.data.f
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable

internal val error = Throwable()

internal fun completableFactory(possibility: Int) = when (possibility) {
	0 -> Completable.complete()
	1 -> Completable.never()
	else -> Completable.error(error)
}

internal fun observableFactory(possibility: Int): ObservableF<Int> = when (possibility) {
	0 -> ObservableF.empty()
	1 -> Observable.never<Int>().f()
	2 -> Observable.error<Int>(error).f()
	3 -> ObservableF.just(0)
	else -> Observable.just(possibility)
		.concatWith(observableFactory(possibility - 3))
		.f()
}

