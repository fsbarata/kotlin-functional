package com.github.fsbarata.functional.control.arrow

import com.github.fsbarata.functional.data.maybe.Optional
import org.junit.Assert.assertEquals
import org.junit.Test

class KleisliTest {
	@Test
	fun compose() {
		val m = Optional.just(3)
		val f = { b: Double -> Optional.just("$b") }
		val g = { a: Int -> Optional.just(a + 0.5) }
		val k = Optional.kleisli(f) compose (Optional.kleisli(g))
		assertEquals(Optional.just("3.5"), m.bind(k))
	}
}
