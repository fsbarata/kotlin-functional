package com.fsbarata.fp.concepts.test

import com.fsbarata.fp.concepts.Applicative
import com.fsbarata.fp.concepts.Monad
import org.junit.Assert.assertTrue
import org.junit.Test

interface MonadTest<C>: ApplicativeTest<C> {
	val monadScope: Monad.Scope<C>
	fun Monad<C, Int>.equalTo(other: Monad<C, Int>): Boolean

	override val applicativeScope: Applicative.Scope<C> get() = monadScope
	override fun Applicative<C, Int>.equalTo(other: Applicative<C, Int>): Boolean =
		(this as Monad<C, Int>).equalTo(other as Monad<C, Int>)

	private val monad get() = monadScope.just(5)

	private fun Monad<C, Int>.multiply(
		x: Int,
	): Monad<C, Int> =
		if (x == 0) scope.just(0)
		else bind { scope.just(x * it) }

	@Test
	fun `flatmap is correct`() {
		assertTrue(monad.map { it * 3 }.equalTo(
			monad.bind { monad.scope.just(it * 3) }
		))
	}

	@Test
	fun `multiply accepts monad`() {
		assertTrue(monadScope.just(15).equalTo(monad.multiply(3)))
		assertTrue(monadScope.just(0).equalTo(monad.multiply(0)))
	}
}