package com.github.fsbarata.functional.control.test

import com.github.fsbarata.functional.control.Applicative
import com.github.fsbarata.functional.control.Functor
import com.github.fsbarata.functional.data.F1
import com.github.fsbarata.functional.data.compose
import com.github.fsbarata.functional.data.id
import org.junit.Test

interface ApplicativeTest<C>: FunctorTest<C> {
	val applicativeScope: Applicative.Scope<C>
	override fun <A> createFunctor(a: A): Applicative<C, A> {
		return applicativeScope.just(a)
	}

	fun Applicative<C, Int>.equalTo(other: Applicative<C, Int>): Boolean
	override fun Functor<C, Int>.equalTo(other: Functor<C, Int>): Boolean =
		(this as Applicative<C, Int>).equalTo(other as Applicative<C, Int>)

	@Test
	fun `just identity`() {
		val v = applicativeScope.just(5)
		val v2 = v.ap(applicativeScope.just(id()))
		assert(v.equalTo(v2))
	}

	@Test
	fun `ap composition`() {
		val u = applicativeScope.just { a: Int -> a * 2 }
		val v = applicativeScope.just { a: Int -> a + 2 }
		val w = applicativeScope.just(5)
		val r1 = w.ap(v).ap(u)
		val comp =
			applicativeScope.just { f1: F1<Int, Int> -> { f2: F1<Int, Int> -> f1.compose(f2) } }
		val r2 = w.ap(v.ap(u.ap(comp)))
		assert(r1.equalTo(r2)) { "$r1 must be equal to $r2" }
	}

	@Test
	fun homorphism() {
		assert(applicativeScope.just(10)
				   .equalTo(applicativeScope.just(5)
								.ap(applicativeScope.just { it * 2 })
				   )
		)
	}

	@Test
	fun interchange() {
		val u = applicativeScope.just { a: Int -> a * 2 }
		val r1 = applicativeScope.just(5).ap(u)
		val r2 = u.ap(applicativeScope.just { it(5) })
		assert(r1.equalTo(r2)) { "$r1 must be equal to $r2" }
	}
}