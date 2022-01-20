package com.github.fsbarata.functional.data.either

import com.github.fsbarata.functional.assertEquals
import com.github.fsbarata.functional.control.MonadLaws
import com.github.fsbarata.functional.data.BiFunctorLaws
import com.github.fsbarata.functional.data.TraversableLaws
import com.github.fsbarata.functional.data.maybe.Optional
import kotlin.test.Test
import kotlin.test.assertNull
import kotlin.test.fail

class EitherTest:
	MonadLaws<EitherContext<String>>,
	TraversableLaws<EitherContext<String>>,
	BiFunctorLaws<EitherBiContext> {
	override val monadScope = Either.Scope<String>()
	override val traversableScope = Either.Scope<String>()

	val error = "some error"
	override val possibilities: Int = 5
	override fun factory(possibility: Int) = when (possibility) {
		0 -> Either.Left(error)
		else -> Either.Right(possibility - 1)
	}

	override fun <A> createTraversable(vararg items: A): Either<String, A> =
		Either.ofNullable(items.firstOrNull()) { error }

	override fun <B, A> createBiFunctor(a: A, b: B) =
		if (b == null) Either.Right(a) else Either.Left(b)

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