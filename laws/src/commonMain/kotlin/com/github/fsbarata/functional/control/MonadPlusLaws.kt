package com.github.fsbarata.functional.control

import kotlin.test.Test

interface MonadPlusLaws<M>: MonadLaws<M> {
	override val monadScope: MonadPlus.Scope<M>

	private fun <A> zero() = monadScope.empty<A>() as MonadPlus<M, A>
	private val zero get() = zero<Any>()

	@Suppress("UNCHECKED_CAST")
	private fun <T> eachPossibilityMonadPlus(block: (MonadPlus<M, Int>) -> T) =
		eachPossibility { block(it as MonadPlus<M, Int>) }

	@Test
	fun `left zero`() {
		assertEqualF(zero(), zero<Int>().bind {
			if (it < 0) monadScope.empty()
			else monadScope.just(it)
		})
		eachPossibilityMonadPlus { mp ->
			assertEqualF(zero(), zero<Int>().bind { mp })
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
			assertEqualF(mp.filterFromBind(f), mp.filter(f))
		}
	}
}
