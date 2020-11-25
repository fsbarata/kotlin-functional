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

	@Test
	fun first() {
		val pass = IllegalStateException()
		val f = { b: Int -> Optional.just("$b") }
		val k = Optional.kleisli(f).first<Exception>()
		assertEquals(Optional.just(Pair("3", pass)), Optional.just(Pair(3, pass)).bind(k))
	}

	@Test
	fun second() {
		val pass = IllegalStateException()
		val f = { b: Int -> Optional.just("$b") }
		val k = Optional.kleisli(f).second<Exception>()
		assertEquals(Optional.just(Pair(pass, "3")), Optional.just(Pair(pass, 3)).bind(k))
	}

	@Test
	fun split() {
		val f = { b: Int -> Optional.just("$b") }
		val g = { a: Long -> Optional.just(a + 0.5) }
		val k = Optional.kleisli(f) split Optional.kleisli(g)
		assertEquals(Optional.just(Pair("3", 3.5)), Optional.just(Pair(3, 3L)).bind(k))
	}

	@Test
	fun fanout() {
		val f = { b: Int -> Optional.just("$b") }
		val g = { a: Int -> Optional.just(a + 0.5) }
		val k = Optional.kleisli(f) fanout Optional.kleisli(g)
		assertEquals(Optional.just(Pair("3", 3.5)), Optional.just(3).bind(k))
	}
}
