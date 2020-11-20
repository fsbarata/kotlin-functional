package com.github.fsbarata.functional.data.either

import com.github.fsbarata.functional.control.test.MonadLaws
import com.github.fsbarata.functional.data.maybe.Optional
import com.github.fsbarata.functional.data.test.BiFunctorLaws
import com.github.fsbarata.functional.data.test.TraversableLaws
import org.junit.Assert.*
import org.junit.Test

class EitherTest:
	MonadLaws<EitherContext>,
	TraversableLaws<EitherContext>,
	BiFunctorLaws<EitherBiContext> {
	override val monadScope = Either
	override val traversableScope = Either

	override fun <A> createTraversable(vararg items: A): Either<Nothing, A> =
		Either.ofNullable(items.firstOrNull()) { throw IllegalStateException() }

	override fun <B, A> createBiFunctor(a: A, b: B) =
		if (b == null) Either.Right(a) else Either.Left(b)

	override fun <A> createFunctor(a: A) = Either.just(a)

	@Test
	fun map() {
		assertEquals(LEFT, LEFT.map { it * 2 })
		assertEquals(Either.Right(10), RIGHT.map { it * 2 })
		assertEquals(Either.Right("5 a"), RIGHT.map { "$it a" })
	}

	@Test
	fun flatMap() {
		assertEquals(LEFT, LEFT.flatMap { Either.Right("$it a") })
		assertEquals(Either.Right("5 a"), RIGHT.flatMap { Either.Right("$it a") })
		assertEquals(LEFT, LEFT.flatMap { Either.Left("$it a") })
		assertEquals(Either.Left("5 a"), RIGHT.flatMap { Either.Left("$it a") })
	}

	@Test
	fun mapLeft() {
		assertEquals(Either.Left("52"), LEFT.mapLeft { it + 2 })
		assertEquals(Either.Left(5), LEFT.mapLeft { it.toInt() })
		assertEquals(RIGHT, RIGHT.mapLeft { it + 2 })
	}

	@Test
	fun bimap() {
		assertEquals(Either.Left("52"), LEFT.bimap({ it + 2 }, { it + 2 }))
		assertEquals(Either.Left(5), LEFT.bimap({ it.toInt() }, { it + 2 }))
		assertEquals(Either.Right(7), RIGHT.bimap({ it + 2 }, { it + 2 }))
		assertEquals(Either.Right("5 + 2"), RIGHT.bimap({ it + 2 }, { "$it + 2" }))
	}

	@Test
	fun fold() {
		assertEquals("5 a", LEFT.fold(ifLeft = { "$it a" }, ifRight = { "$it b" }))
		assertEquals("5 b", RIGHT.fold(ifLeft = { "$it a" }, ifRight = { "$it b" }))
		LEFT.fold(ifLeft = {}, ifRight = { fail() })
		RIGHT.fold(ifLeft = { fail() }, ifRight = {})
	}

	@Test
	fun orNull() {
		assertNull(LEFT.orNull())
		assertEquals(5, RIGHT.orNull())
	}

	@Test
	fun toOptional() {
		assertEquals(Optional.empty<Int>(), LEFT.toOptional())
		assertEquals(Optional.just(5), RIGHT.toOptional())
	}

	@Test
	fun swap() {
		assertEquals(Either.Right("5"), LEFT.swap())
		assertEquals(Either.Left(5), RIGHT.swap())
	}

	@Test
	fun orElse() {
		assertEquals(1239, LEFT.orElse(1239))
		assertEquals(5, RIGHT.orElse(1239))
	}

	@Test
	fun valueOr() {
		assertEquals(6, LEFT.valueOr { it.toInt() + 1 })
		assertEquals(5, RIGHT.valueOr { fail() })
	}

	companion object {
		private val LEFT: Either<String, Int> = Either.Left("5")
		private val RIGHT: Either<String, Int> = Either.Right(5)
	}
}