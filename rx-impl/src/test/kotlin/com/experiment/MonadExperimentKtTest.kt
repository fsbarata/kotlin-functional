package com.experiment

import com.fsbarata.fp.concepts.Monad
import com.fsbarata.fp.types.f
import com.fsbarata.fp.types.asObservable
import io.reactivex.subjects.PublishSubject
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class MonadExperimentKtTest {
	private fun <F : Any> Monad<F, Int>.multiply(
			x: Int
	): Monad<F, Int> =
			if (x == 0) just(0)
			else flatMap { just(x * it) }

	@Test
	fun `multiply accepts Observable`() {
		val subject = PublishSubject.create<Int>()
		val testObserver =
				subject.f()
						.multiply(5)
						.asObservable
						.test()

		assertTrue(subject.hasObservers())

		subject.onNext(3)
		testObserver.assertValue(15)

		subject.onNext(5)
		testObserver.assertValueAt(1, 25)

		testObserver.assertNotTerminated()
		testObserver.dispose()

		val anotherObserver =
				subject.f()
						.multiply(0)
						.asObservable
						.test()

		anotherObserver.assertValue(0)
		assertFalse(subject.hasObservers())

		anotherObserver.assertComplete()
	}
}