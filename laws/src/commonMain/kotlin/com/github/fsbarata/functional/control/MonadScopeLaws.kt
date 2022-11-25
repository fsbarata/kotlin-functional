package com.github.fsbarata.functional.control

import com.github.fsbarata.functional.Context
import kotlin.test.Test

interface MonadScopeLaws<M>: ApplicativeScopeLaws<M> {
	val monadScope: Monad.Scope<M>

	override val applicativeScope: Applicative.Scope<M> get() = monadScope

	@Test
	fun `scope bind left identity`() {
		val a = 7
		val k = { x: Int -> monadScope.just(x * 3) }
		val r1 = k(a)
		val r2 = monadScope.bind(monadScope.just(a), k)
		assertEqualF(r1, r2)
	}

	@Test
	fun `cope bind right identity`() {
		eachPossibility { m ->
			val b = monadScope.bind(m) { monadScope.just(it) }
			assertEqualF(m, b)
		}
	}

	@Test
	fun `cope bind associativity`() {
		eachPossibility { m ->
			val k = { a: Int -> monadScope.just(a + 2) }
			val h = { a: Int -> monadScope.just(a * 2) }
			val r1 = monadScope.bind(m) { x -> monadScope.bind(k(x), h) }
			val r2 = monadScope.bind(monadScope.bind(m, k), h)
			assertEqualF(r1, r2)
		}
	}

	@Test
	fun `scope ap is correct`() {
		eachPossibility { m ->
			val mf = monadScope.just { a: Int -> a * 2 }
			val r1 = monadScope.ap(m, mf)
			val r2 = monadScope.bind(mf) { x1 -> monadScope.bind(m) { x2 -> monadScope.just(x1(x2)) } }
			assertEqualF(r1, r2)
		}
	}

	@Test
	fun `scope lift2 is correct`() {
		eachPossibility { m1 ->
			eachPossibility { m2 ->
				val f = { a: Int, b: Int -> (a + 2) * 3 + b }
				val r1 = monadScope.lift2(m1, m2, f)
				val r2 = monadScope.bind(m1) { x1 -> monadScope.bind(m2) { x2 -> monadScope.just(f(x1, x2)) } }
				assertEqualF(r1, r2)
			}
		}
	}

	@Test
	fun `scope map is correct`() {
		eachPossibility { m ->
			assertEqualF(
				monadScope.map(m) { it * 3 },
				monadScope.bind(m) { monadScope.just(it * 3) })
		}
	}

	private fun Monad.Scope<M>.multiply(
		x: Context<M, Int>,
		y: Int,
	): Context<M, Int> =
		if (y == 0) just(0)
		else bind(x) { just(y * it) }

	@Test
	fun `scope multiply accepts monad`() {
		assertEqualF(monadScope.just(15), monadScope.multiply(monadScope.just(5), 3))
		assertEqualF(monadScope.just(0), monadScope.multiply(monadScope.just(5), 0))
		eachPossibility { assertEqualF(monadScope.just(0), monadScope.multiply(it, 0)) }
	}
}