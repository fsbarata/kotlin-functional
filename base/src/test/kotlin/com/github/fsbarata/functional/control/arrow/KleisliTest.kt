package com.github.fsbarata.functional.control.arrow

import com.github.fsbarata.functional.data.either.Either
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

	@Test
	fun left() {
		val pass = IllegalStateException()
		val f = { b: Int -> Optional.just("$b") }
		val k = Optional.kleisli(f).left<Exception>()
		assertEquals(Optional.just(Either.Left("3")), Optional.just(Either.Left(3)).bind(k))
		assertEquals(Optional.just(Either.Right(pass)), Optional.just(Either.Right(pass)).bind(k))
	}

	@Test
	fun right() {
		val pass = IllegalStateException()
		val f = { b: Int -> Optional.just("$b") }
		val k = Optional.kleisli(f).right<Exception>()
		assertEquals(Optional.just(Either.Left(pass)), Optional.just(Either.Left(pass)).bind(k))
		assertEquals(Optional.just(Either.Right("3")), Optional.just(Either.Right(3)).bind(k))
	}

	@Test
	fun splitChoice() {
		val f = { b: Int -> Optional.just("$b") }
		val g = { a: Long -> Optional.just(a + 0.5) }
		val k = Optional.kleisli(f) splitChoice Optional.kleisli(g)
		assertEquals(Optional.just(Either.Left("3")), Optional.just(Either.Left(3)).bind(k))
		assertEquals(Optional.just(Either.Right(3.5)), Optional.just(Either.Right(3L)).bind(k))
	}

	@Test
	fun fanin() {
		val f = { b: Int -> Optional.just("$b") }
		val g = { a: Double -> Optional.just("$a") }
		val k = Optional.kleisli(f) fanin Optional.kleisli(g)
		assertEquals(Optional.just("3"), Optional.just(Either.Left(3)).bind(k))
		assertEquals(Optional.just("2.5"), Optional.just(Either.Right(2.5)).bind(k))
	}
}
