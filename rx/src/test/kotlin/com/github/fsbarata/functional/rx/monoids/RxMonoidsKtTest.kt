package com.github.fsbarata.functional.rx.monoids

import com.github.fsbarata.functional.data.monoid.productIntMonoid
import com.github.fsbarata.functional.data.test.MonoidLaws
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.core.Observable
import org.junit.Test

private val error = Throwable()

private fun completableFactory(possibility: Int) = when (possibility) {
	0 -> Completable.complete()
	1 -> Completable.never()
	else -> Completable.error(error)
}

class ConcatCompletableMonoidTest: MonoidLaws<Completable>(
	concatCompletableMonoid(),
) {
	override val possibilities: Int = 3
	override fun nonEmpty(possibility: Int) = completableFactory(possibility)

	override fun equals(a1: Completable, a2: Completable): Boolean {
		val observer1 = a1.materialize<Unit>().test()
		val observer2 = a2.materialize<Unit>().test()
		return observer1.values() == observer2.values()
	}
}

private fun observableFactory(possibility: Int) = when (possibility) {
	0 -> Observable.empty()
	1 -> Observable.never()
	2 -> Observable.error(error)
	else -> Observable.just(possibility)
}

class ConcatObservableMonoidTest: MonoidLaws<Observable<Int>>(
	concatObservableMonoid<Int>(),
) {
	override val possibilities: Int = 5
	override fun nonEmpty(possibility: Int) = observableFactory(possibility)

	override fun equals(a1: Observable<Int>, a2: Observable<Int>): Boolean {
		val observer1 = a1.materialize().test()
		val observer2 = a2.materialize().test()
		return observer1.values() == observer2.values()
	}
}

class MaybeSumMonoidTest: MonoidLaws<Maybe<Int>>(
	maybeMonoid(productIntMonoid()),
) {
	override val possibilities: Int = 100
	override fun nonEmpty(possibility: Int) = Maybe.just(possibility)

	override fun equals(a1: Maybe<Int>, a2: Maybe<Int>): Boolean {
		val observer1 = a1.materialize().test()
		val observer2 = a2.materialize().test()
		return observer1.values() == observer2.values()
	}

	@Test
	fun combines() {
		with(maybeMonoid(productIntMonoid())) {
			assert(equals(
				Maybe.just(5),
				combine(Maybe.just(5), Maybe.empty())
			))

			assert(equals(
				Maybe.just(2),
				combine(Maybe.empty(), Maybe.just(2))
			))

			assert(equals(
				Maybe.just(4),
				combine(Maybe.just(2), Maybe.just(2))
			))
		}
	}
}
