package com.github.fsbarata.functional.control

import kotlin.test.Test

interface MonadPlusLaws<M>: MonadLaws<M> {
	override val monadScope: MonadPlus.Scope<M>

	private val empty get() = monadScope.empty<Any>() as MonadPlus<M, Any>

	@Suppress("UNCHECKED_CAST")
	private fun <T> eachPossibilityMonadPlus(block: (MonadPlus<M, Int>) -> T) =
		eachPossibility { block(it as MonadPlus<M, Int>) }

	@Test
	fun `left zero`() {
		eachPossibilityMonadPlus { mp ->
			assertEqualF(empty, empty.bind { mp })
		}
	}

	@Test
	fun `right zero`() {
		eachPossibilityMonadPlus { mp ->
			assertEqualF(empty, mp.bind { empty })
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
