package com.github.fsbarata.functional.control

import kotlin.test.Test

interface MonadLaws<M>: ApplicativeLaws<M>, MonadScopeLaws<M> {
	@Suppress("UNCHECKED_CAST")
	private fun eachPossibilityMonad(block: (Monad<M, Int>) -> Unit) {
		eachPossibility { block(it as Monad<M, Int>) }
	}

	fun <A> just(a: A): Monad<M, A> = monadScope.just(a) as Monad<M, A>

	@Test
	fun `bind left identity`() {
		val a = 7
		val k = { x: Int -> just(x * 3) }
		val r1 = k(a)
		val r2 = just(a).bind { k(it) }
		assertEqualF(r1, r2)
	}

	@Test
	fun `bind right identity`() {
		eachPossibilityMonad { m ->
			val b = m.bind { just(it) }
			assertEqualF(m, b)
		}
	}

	@Test
	fun `bind associativity`() {
		eachPossibilityMonad { m ->
			val k = { a: Int -> just(a + 2) }
			val h = { a: Int -> just(a * 2) }
			val r1 = m.bind { x -> k(x).bind(h) }
			val r2 = m.bind(k).bind(h)
			assertEqualF(r1, r2)
		}
	}

	@Test
	fun `ap is correct`() {
		eachPossibilityMonad { m ->
			val mf = just { a: Int -> a * 2 }
			val r1 = m.ap(mf)
			val r2 = mf.bind { x1 -> m.bind { x2 -> just(x1(x2)) } }
			assertEqualF(r1, r2)
		}
	}

	@Test
	fun `lift2 is correct`() {
		eachPossibilityMonad { m1 ->
			eachPossibilityMonad { m2 ->
				val f = { a: Int, b: Int -> (a + 2) * 3 + b }
				val r1 = m1.lift2(m2, f)
				val r2 = m1.bind { x1 -> m2.bind { x2 -> just(f(x1, x2)) } }
				assertEqualF(r1, r2)
			}
		}
	}

	@Test
	fun `map is correct`() {
		eachPossibilityMonad { m ->
			assertEqualF(
				m.map { it * 3 },
				m.bind { just(it * 3) })
		}
	}
}