package com.github.fsbarata.functional.control.test

import com.github.fsbarata.functional.control.MonadZip
import com.github.fsbarata.functional.control.unzip
import com.github.fsbarata.functional.control.zip
import com.github.fsbarata.functional.data.split
import org.junit.Test

interface MonadZipLaws<M>: MonadLaws<M> {
	private val monad1 get() = monadScope.just(9) as MonadZip<M, Int>
	private val monad2 get() = monadScope.just(3) as MonadZip<M, Int>

	@Test
	fun naturality() {
		val f1 = { a: Int -> a * 2 }
		val f2 = { a: Int -> a + 2 }
		val f3 = { a: Int, b: Int -> a / b }
		val r1 =
			zip(monad1, monad2).map(f1 split f2)
				.map { (a, b) -> f3(a, b) }
		val r2 =
			zip(monad1.map(f1) as MonadZip<M, Int>,
				monad2.map(f2) as MonadZip<M, Int>,
				f3)
		assertEqual(r1, r2)
	}

	@Test
	fun `information preservation`() {
		val (r1, r2) = unzip(zip(monad1, monad2))
		assertEqual(r1, monad1)
		assertEqual(r2, monad2)
	}
}