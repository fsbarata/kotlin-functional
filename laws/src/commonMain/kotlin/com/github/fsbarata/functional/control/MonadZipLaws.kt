package com.github.fsbarata.functional.control

import com.github.fsbarata.functional.control.arrow.split
import com.github.fsbarata.functional.control.arrow.splitWith
import com.github.fsbarata.functional.data.Functor
import kotlin.test.Test

interface MonadZipLaws<M>: MonadLaws<M> {
	fun <A, B, R> zip(arg1: Monad<M, A>, arg2: Functor<M, B>, f: (A, B) -> R): Monad<M, R> =
		(arg1 as MonadZip<M, A>).zipWith(arg2, f)


	@Suppress("UNCHECKED_CAST")
	private fun <T> eachPossibilityMonad(block: (Monad<M, Int>) -> T) =
		eachPossibility { block(it as Monad<M, Int>) }

	@Test
	fun `zip naturality`() {
		eachPossibilityMonad { mz1 ->
			eachPossibilityMonad { mz2 ->
				val f1 = { a: Int -> a * 2 }
				val f2 = { a: Int -> a + 2 }
				val f3 = { a: Int, b: Int -> a / b }
				val r1 =
					zip(mz1, mz2, ::Pair).map(f1 splitWith f2)
						.map { (a, b) -> f3(a, b) }
				val r2 = zip(mz1.map(f1), mz2.map(f2), f3)
				assertEqualF(r1, r2)
			}
		}
	}

	@Test
	fun `zip information preservation`() {
		eachPossibilityMonad { mz1 ->
			eachPossibilityMonad { mz2 ->
				val mconst1 = mz1.map { 10 }
				val mconst2 = mz2.map { 10 }
				if (mconst1.equalTo(mconst2)) {
					val (r1, r2) = unzip(zip(mz1, mz2, ::Pair))
					assertEqualF(r1, mz1)
					assertEqualF(r2, mz2)
				}
			}
		}
	}
}