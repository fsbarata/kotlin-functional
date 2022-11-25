package com.github.fsbarata.functional.control

import com.github.fsbarata.functional.data.*
import kotlin.math.roundToInt
import kotlin.test.Test

interface ApplicativeScopeLaws<F>: FunctorScopeLaws<F> {
	override fun factory(possibility: Int): Functor<F, Int>

	val applicativeScope: Applicative.Scope<F>
	override val functorScope: Functor.Scope<F> get() = applicativeScope

	@Test
	fun `just identity`() {
		eachPossibility { r1 ->
			val r2 = applicativeScope.ap(r1, applicativeScope.just(id()))
			assertEqualF(r1, r2)
		}
	}

	@Test
	fun `scope ap composition`() {
		eachPossibility { w ->
			val u = applicativeScope.just { a: Int -> a * 2 }
			val v = applicativeScope.just { a: Int -> a + 2 }
			val r1 = applicativeScope.ap(applicativeScope.ap(w, v), u)
			val comp =
				applicativeScope.just { f1: F1<Int, Int> -> { f2: F1<Int, Int> -> f1.compose(f2) } }
			val r2 = applicativeScope.ap(w, applicativeScope.ap(v, applicativeScope.ap(u, comp)))
			assertEqualF(r1, r2)
		}
	}

	@Test
	fun `scope ap homomorphism`() {
		val r1 = applicativeScope.just(10)
		val r2 = applicativeScope.ap(
			applicativeScope.just(5),
			applicativeScope.just { it * 2 }
		)
		assertEqualF(r1, r2)
	}

	@Test
	fun `scope ap interchange`() {
		val u = applicativeScope.just { a: Int -> a * 2 }
		val r1 = applicativeScope.ap(applicativeScope.just(5), u)
		val r2 = applicativeScope.ap(u, applicativeScope.just { it(5) })
		assertEqualF(r1, r2)
	}

	@Test
	fun `scope lift2 = lift2FromAp`() {
		eachPossibility { u ->
			eachPossibility { v ->
				val v2 = applicativeScope.map(v) { it + 0.5 }
				val f = { a: Int, b: Double -> (a * b).toString() }
				val r1 = lift2FromAp(applicativeScope, u, v2, f)
				val r2 = applicativeScope.lift2(u, v2, f)
				assertEqualF(r1, r2)
			}
		}
	}

	@Test
	fun `scope ap = apFromLift2`() {
		val u = applicativeScope.just(5)
		val f = applicativeScope.just { a: Int -> (a * 0.5).roundToInt() }
		val r1 = apFromLift2(applicativeScope, u, f)
		val r2 = applicativeScope.ap(u, f)
		assertEqualF(r1, r2)
	}
}