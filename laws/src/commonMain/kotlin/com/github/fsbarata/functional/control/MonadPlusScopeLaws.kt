package com.github.fsbarata.functional.control

import kotlin.test.Test

interface MonadPlusScopeLaws<M>: MonadScopeLaws<M> {
	override val monadScope: MonadPlus.Scope<M>
	private val zero get() = monadScope.empty<Any>()

	@Test
	fun `scope left zero`() {
		with(monadScope) {
			assertEqualF(empty(), bind(empty<Int>()) {
				if (it < 0) empty()
				else just(it)
			})
			eachPossibility { mp ->
				assertEqualF(empty(), bind(empty<Int>()) { mp })
			}
		}
	}

	@Test
	fun `scope right zero`() {
		with(monadScope) {
			eachPossibility { mp ->
				assertEqualF(zero, andThen(mp, zero))
			}
		}
	}

	@Test
	fun `scope filter from bind`() {
		with(monadScope) {
			eachPossibility { mp ->
				val f = { a: Int -> a > 3 }
				assertEqualF(bind(mp, filterKleisli(f)), filter(mp, f))
			}
		}
	}
}
