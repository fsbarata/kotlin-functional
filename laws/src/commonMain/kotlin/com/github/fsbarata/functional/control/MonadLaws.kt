package com.github.fsbarata.functional.control

import com.github.fsbarata.functional.Context
import kotlin.test.Test

interface MonadLaws<M>: ApplicativeLaws<M> {
	val monadScope: Monad.Scope<M>

	@Suppress("UNCHECKED_CAST")
	private fun <T> eachPossibilityMonad(block: (Monad<M, Int>) -> T) =
		eachPossibility { block(it as Monad<M, Int>) }

	override val applicativeScope: Applicative.Scope<M> get() = monadScope

	@Test
	fun `bind left identity`() {
		val a = 7
		val k = { x: Int -> monadScope.just(x * 3) }
		val r1 = k(a)
		val r2 = monadScope.bind(monadScope.just(a), k)
		assertEqualF(r1, r2)
	}

	@Test
	fun `bind right identity`() {
		eachPossibilityMonad { m ->
			val b = m.bind { monadScope.just(it) }
			assertEqualF(m, b)
		}
	}

	@Test
	fun `bind associativity`() {
		eachPossibilityMonad { m ->
			val k = { a: Int -> monadScope.just(a + 2) }
			val h = { a: Int -> monadScope.just(a * 2) }
			val r1 = m.bind { x -> monadScope.bind(k(x), h) }
			val r2 = m.bind(k).bind(h)
			assertEqualF(r1, r2)
		}
	}

	@Test
	fun `ap is correct`() {
		eachPossibilityMonad { m ->
			val mf = monadScope.just { a: Int -> a * 2 }
			val r1 = m.ap(mf)
			val r2 = monadScope.bind(mf) { x1 -> m.bind { x2 -> monadScope.just(x1(x2)) } }
			assertEqualF(r1, r2)
		}
	}

	@Test
	fun `lift2 is correct`() {
		eachPossibilityMonad { m1 ->
			eachPossibilityMonad { m2 ->
				val f = { a: Int, b: Int -> (a + 2) * 3 + b }
				val r1 = m1.lift2(m2, f)
				val r2 = m1.bind { x1 -> m2.bind { x2 -> monadScope.just(f(x1, x2)) } }
				assertEqualF(r1, r2)
			}
		}
	}

	@Test
	fun `map is correct`() {
		eachPossibilityMonad { m ->
			assertEqualF(
				m.map { it * 3 },
				m.bind { monadScope.just(it * 3) })
		}
	}

	private fun Monad.Scope<M>.multiply(
		x: Context<M, Int>,
		y: Int,
	): Context<M, Int> =
		if (y == 0) just(0)
		else bind(x) { just(y * it) }

	@Test
	fun `multiply accepts monad`() {
		assertEqualF(monadScope.just(15), monadScope.multiply(monadScope.just(5), 3))
		assertEqualF(monadScope.just(0), monadScope.multiply(monadScope.just(5), 0))
		eachPossibilityMonad { assertEqualF(monadScope.just(0), monadScope.multiply(it, 0)) }
	}
}