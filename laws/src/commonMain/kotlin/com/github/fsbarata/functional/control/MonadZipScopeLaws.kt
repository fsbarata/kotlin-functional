package com.github.fsbarata.functional.control

import com.github.fsbarata.functional.control.arrow.splitWith
import com.github.fsbarata.functional.data.Functor
import kotlin.test.Test

interface MonadZipScopeLaws<M>: MonadScopeLaws<M> {
	override val monadScope: MonadZip.Scope<M>

	@Test
	fun `scope zip naturality`() {
		with(monadScope) {
			eachPossibility { mz1 ->
				eachPossibility { mz2 ->
					val f1 = { a: Int -> a * 2 }
					val f2 = { a: Int -> a + 2 }
					val f3 = { a: Int, b: Int -> a / b }
					val r1 =
						map(map(zip(mz1, mz2, ::Pair), f1 splitWith f2)) { (a, b) -> f3(a, b) }
					val r2 = zip(map(mz1, f1), map(mz2, f2), f3)
					assertEqualF(r1, r2)
				}
			}
		}
	}

	@Test
	fun `scope zip information preservation`() {
		with(monadScope) {
			eachPossibility { mz1 ->
				eachPossibility { mz2 ->
					val mconst1 = map(mz1) { 10 }
					val mconst2 = map(mz2) { 10 }
					if (mconst1.equalTo(mconst2)) {
						val (r1, r2) = unzip(zip(mz1, mz2, ::Pair))
						assertEqualF(r1, mz1)
						assertEqualF(r2, mz2)
					}
				}
			}
		}
	}
}