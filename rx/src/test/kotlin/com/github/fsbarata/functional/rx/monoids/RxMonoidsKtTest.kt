package com.github.fsbarata.functional.rx.monoids

import com.github.fsbarata.functional.data.monoid.productIntMonoid
import com.github.fsbarata.functional.data.test.MonoidTest
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.core.Observable
import org.junit.Test
import kotlin.random.Random

private val error = Throwable()

private val completableFactory = {
	when {
		Random.nextBoolean() -> Completable.complete()
		else -> when (Random.nextBoolean()) {
			false -> Completable.never()
			true -> Completable.error(error)
		}
	}
}

class ConcatCompletableMonoidTest: MonoidTest<Completable>(
	concatCompletableMonoid(),
	completableFactory
) {
	override fun equals(a1: Completable, a2: Completable): Boolean {
		val observer1 = a1.materialize<Unit>().test()
		val observer2 = a2.materialize<Unit>().test()
		return observer1.values() == observer2.values()
	}
}

private fun observableFactory(): Observable<Int> = when {
	Random.nextBoolean() -> when (Random.nextBoolean()) {
		false -> Observable.empty()
		true -> Observable.just(Random.nextInt()).concatWith(observableFactory())
	}
	else -> when (Random.nextBoolean()) {
		false -> Observable.never()
		true -> Observable.error(error)
	}
}

class ConcatObservableMonoidTest: MonoidTest<Observable<Int>>(
	concatObservableMonoid<Int>(),
	::observableFactory
) {
	override fun equals(a1: Observable<Int>, a2: Observable<Int>): Boolean {
		val observer1 = a1.materialize().test()
		val observer2 = a2.materialize().test()
		return observer1.values() == observer2.values()
	}
}

class MaybeSumMonoidTest: MonoidTest<Maybe<Int>>(
	maybeMonoid(productIntMonoid()),
	{ Maybe.just(Random.nextInt(1, 100)) }
) {
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
