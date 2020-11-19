package com.github.fsbarata.functional.control.test

import com.github.fsbarata.functional.control.Applicative
import com.github.fsbarata.functional.control.Monad
import org.junit.Test

interface MonadLaws<M>: ApplicativeLaws<M> {
	val monadScope: Monad.Scope<M>

	override val applicativeScope: Applicative.Scope<M> get() = monadScope

	private val monad get() = monadScope.just(5)

	@Test
	fun `bind left identity`() {
		val a = 7
		val k = { x: Int -> monadScope.just(x * 3) }
		val r1 = k(a)
		val r2 = monadScope.just(a).bind(k)
		assertEqualF(r1, r2)
	}

	@Test
	fun `bind right identity`() {
		val m = monadScope.just(7)
		val b = m.bind { monadScope.just(it) }
		assertEqualF(m, b)
	}

	@Test
	fun `bind associativity`() {
		val m = monadScope.just(9)
		val k = { a: Int -> monadScope.just(a + 2) }
		val h = { a: Int -> monadScope.just(a * 2) }
		val r1 = m.bind { x -> k(x).bind { h(it) } }
		val r2 = m.bind(k).bind(h)
		assertEqualF(r1, r2)
	}

	@Test
	fun `ap is correct`() {
		val m1 = monadScope.just { a: Int -> a * 2 }
		val m2 = monadScope.just(5)
		val r1 = m2.ap(m1)
		val r2 = m1.bind { x1 -> m2.bind { x2 -> monadScope.just(x1(x2)) } }
		assertEqualF(r1, r2)
	}

	@Test
	fun `map is correct`() {
		assertEqualF(monad.map { it * 3 }, monad.bind { monad.scope.just(it * 3) })
	}

	private fun Monad<M, Int>.multiply(
		x: Int,
	): Monad<M, Int> =
		if (x == 0) scope.just(0)
		else bind { scope.just(x * it) }

	@Test
	fun `multiply accepts monad`() {
		assertEqualF(monadScope.just(15), monad.multiply(3))
		assertEqualF(monadScope.just(0), monad.multiply(0))
	}
}