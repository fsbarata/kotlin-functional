package com.github.fsbarata.functional.control

import kotlin.test.Test

interface MonadPlusLaws<M>: MonadLaws<M>, MonadPlusScopeLaws<M> {
	private fun <A> empty() = monadScope.empty<A>() as MonadPlus<M, A>
	private val zero get() = empty<Any>()

	@Suppress("UNCHECKED_CAST")
	private fun <T> eachPossibilityMonadPlus(block: (MonadPlus<M, Int>) -> T) =
		eachPossibility { block(it as MonadPlus<M, Int>) }

	@Test
	fun `left zero`() {
		assertEqualF(empty(), empty<Int>().bind {
			if (it < 0) monadScope.empty()
			else monadScope.just(it)
		})
		eachPossibility { mp ->
			assertEqualF(empty(), empty<Int>().bind { mp })
		}
	}

	@Test
	fun `right zero`() {
		eachPossibilityMonadPlus { mp ->
			assertEqualF(zero, mp.andThen(zero))
		}
	}

	@Test
	fun `filter from bind`() {
		eachPossibilityMonadPlus { mp ->
			val f = { a: Int -> a > 3 }
			assertEqualF(mp.bind(mp.scope.filterKleisli(f)), mp.filter(f))
		}
	}
}
