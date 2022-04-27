package com.github.fsbarata.functional.data.rx

import com.github.fsbarata.functional.control.lift
import com.github.fsbarata.functional.data.maybe.Optional
import com.github.fsbarata.functional.data.maybe.invoke
import com.github.fsbarata.functional.data.maybe.liftOpt
import com.github.fsbarata.functional.data.maybe.liftOpt2
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.PublishSubject
import org.junit.Test

class RxCompositionsTest {
	@Test
	fun composeOptional() {
		val observer1 = Observable.just(Optional.just(3))
			.map(lift { a: Int -> a + 2 }::invoke)
			.test()
		observer1.assertValue(Optional.just(5))

		val observer2 = Observable.just(Optional.just(3))
			.map(liftOpt { a: Int -> a + 2 })
			.test()
		observer2.assertValue(Optional.just(5))

		val observer3 = Observable.just(Optional.empty<Int>())
			.map(lift { a: Int -> a + 2 }::invoke)
			.test()
		observer3.assertValue(Optional.empty())

		val observer4 = Observable.just(Optional.empty<Int>())
			.map(liftOpt { a: Int -> a + 2 })
			.test()
		observer4.assertValue(Optional.empty())
	}

	@Test
	fun compose2Optionals() {
		val subject1 = PublishSubject.create<Optional<Int>>()
		val subject2 = PublishSubject.create<Optional<Int>>()
		val observer = Observable.combineLatest(
			subject1,
			subject2,
			liftOpt2 { a: Int, b: Int -> a + b },
		)
			.map(lift { a: Int -> a + 2 }::invoke)
			.test()

		subject1.onNext(Optional.just(3))
		subject2.onNext(Optional.just(5))
		subject1.onNext(Optional.empty())
		subject2.onNext(Optional.just(4))
		subject1.onNext(Optional.just(5))

		observer.assertValues(
			Optional.just(10),
			Optional.empty(),
			Optional.empty(),
			Optional.just(11),
		)
	}
}