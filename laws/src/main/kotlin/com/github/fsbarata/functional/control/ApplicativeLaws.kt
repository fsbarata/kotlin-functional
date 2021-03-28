package com.github.fsbarata.functional.control

import com.github.fsbarata.functional.data.F1
import com.github.fsbarata.functional.data.FunctorLaws
import com.github.fsbarata.functional.data.compose
import org.junit.Test
import kotlin.math.roundToInt

interface ApplicativeLaws<F>: ApplicativeScopeLaws<F>, FunctorLaws<F> {
	@Suppress("UNCHECKED_CAST")
	private fun <T> eachPossibilityApp(block: (Applicative<F, Int>) -> T): List<T> {
		return eachPossibility { block(it as Applicative<F, Int>) }
	}

	private fun <A> just(a: A) = applicativeScope.just(a) as Applicative<F, A>

	@Test
	fun `ap composition`() {
		eachPossibilityApp { w ->
			val u = just { a: Int -> a * 2 }
			val v = just { a: Int -> a + 2 }
			val r1 = w ap v ap u
			val comp = just { f1: F1<Int, Int> -> { f2: F1<Int, Int> -> f1.compose(f2) } }
			val r2 = w ap (v ap (u ap (comp)))
			assertEqualF(r1, r2)
		}
	}

	@Test
	fun `ap homomorphism`() {
		val r1 = just(10)
		val r2 = just(5).ap(just { it * 2 })
		assertEqualF(r1, r2)
	}

	@Test
	fun `ap interchange`() {
		val u = just { a: Int -> a * 2 }
		val r1 = just(5).ap(u)
		val r2 = u.ap(just { it(5) })
		assertEqualF(r1, r2)
	}

	@Test
	fun `lift2 = lift2FromAp`() {
		eachPossibilityApp { u ->
			eachPossibilityApp { v ->
				val v2 = v.map { it + 0.5 }
				val f = { a: Int, b: Double -> (a * b).toString() }
				val r1 = lift2FromAp(applicativeScope, u, v2, f)
				val r2 = u.lift2(v2, f)
				assertEqualF(r1, r2)
			}
		}
	}

	@Test
	fun `ap = apFromLift2`() {
		val u = just(5)
		val f = just { a: Int -> (a * 0.5).roundToInt() }
		val r1 = apFromLift2(applicativeScope, u, f)
		val r2 = u.ap(f)
		assertEqualF(r1, r2)
	}
}