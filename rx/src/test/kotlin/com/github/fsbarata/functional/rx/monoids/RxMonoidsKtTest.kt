package com.github.fsbarata.functional.rx.monoids

import com.github.fsbarata.functional.data.MonoidLaws
import com.github.fsbarata.functional.rx.completableFactory
import com.github.fsbarata.functional.rx.observableFactory
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable


class ConcatCompletableMonoidTest: MonoidLaws<Completable>(
	concatCompletableMonoid(),
) {
	override val possibilities: Int = 3
	override fun factory(possibility: Int) = completableFactory(possibility)

	override fun equals(a1: Completable, a2: Completable): Boolean {
		val observer1 = a1.materialize<Unit>().test()
		val observer2 = a2.materialize<Unit>().test()
		return observer1.values() == observer2.values()
	}
}

class ConcatObservableMonoidTest: MonoidLaws<Observable<Int>>(
	concatObservableMonoid<Int>(),
) {
	override val possibilities: Int = 5
	override fun factory(possibility: Int) = observableFactory(possibility)

	override fun equals(a1: Observable<Int>, a2: Observable<Int>): Boolean {
		val observer1 = a1.materialize().test()
		val observer2 = a2.materialize().test()
		return observer1.values() == observer2.values()
	}
}
