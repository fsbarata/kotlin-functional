package com.github.fsbarata.functional.control

import com.github.fsbarata.functional.data.split
import org.junit.Test

interface MonadZipLaws<M>: MonadLaws<M> {
	private fun <T> eachPossibilityMonadZip(block: (MonadZip<M, Int>) -> T) =
		eachPossibility { block(it as MonadZip<M, Int>) }

	@Test
	fun `zip naturality`() {
		eachPossibilityMonadZip { mz1 ->
			eachPossibilityMonadZip { mz2 ->
				val f1 = { a: Int -> a * 2 }
				val f2 = { a: Int -> a + 2 }
				val f3 = { a: Int, b: Int -> a / b }
				val r1 =
					zip(mz1, mz2).map(f1 split f2)
						.map { (a, b) -> f3(a, b) }
				val r2 =
					zip(mz1.map(f1) as MonadZip<M, Int>,
						mz2.map(f2) as MonadZip<M, Int>,
						f3)
				assertEqualF(r1, r2)
			}
		}
	}

	@Test
	fun `zip information preservation`() {
		eachPossibilityMonadZip { mz1 ->
			eachPossibilityMonadZip { mz2 ->
				val mconst1 = mz1.map { 10 }
				val mconst2 = mz2.map { 10 }
				if (mconst1.equalTo(mconst2)) {
					val (r1, r2) = unzip(zip(mz1, mz2))
					assertEqualF(r1, mz1)
					assertEqualF(r2, mz2)
				}
			}
		}
	}
}