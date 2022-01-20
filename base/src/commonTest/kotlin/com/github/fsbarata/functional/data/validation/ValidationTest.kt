package com.github.fsbarata.functional.data.validation

import com.github.fsbarata.functional.Context
import com.github.fsbarata.functional.assertEquals
import com.github.fsbarata.functional.data.BiFunctorLaws
import com.github.fsbarata.functional.data.FunctorLaws
import com.github.fsbarata.functional.data.either.Either
import com.github.fsbarata.functional.data.either.flatMap
import com.github.fsbarata.functional.data.list.nelOf
import com.github.fsbarata.functional.data.maybe.Optional
import com.github.fsbarata.functional.data.validation.Validation.Failure
import com.github.fsbarata.functional.data.validation.Validation.Success
import kotlin.test.Test
import kotlin.test.fail

@Suppress("UNREACHABLE_CODE")
class ValidationTest:
	FunctorLaws<ValidationContext<String>>,
	BiFunctorLaws<ValidationBiContext> {
	override fun <B, A> createBiFunctor(a: A, b: B) =
		if (a == null) Failure(b) else Success(a)

	val error = "some error"
	override val possibilities: Int = 5
	override fun factory(possibility: Int) = when (possibility) {
		0 -> Failure(error)
		else -> Success(possibility - 1)
	}

	override fun <A> Context<ValidationContext<String>, A>.equalTo(other: Context<ValidationContext<String>, A>) =
		asValidation == other.asValidation

	@Test
	fun fold() {
		assertEquals("3", Success(3).fold(ifFailure = { fail() }, ifSuccess = { "$it" }))
		assertEquals(5, Failure(3).fold(ifFailure = { it + 2 }, ifSuccess = { fail() }))
	}

	@Test
	fun mapLeft() {
		assertEquals(Failure("3"), Failure(3).mapLeft { "$it" })
		assertEquals(Success(3), Success(3).mapLeft { fail() })
	}

	@Test
	fun map() {
		assertEquals(Success("3"), Success(3).map { "$it" })
		assertEquals(Failure(3), Failure(3).map { fail() })
	}

	@Test
	fun fromEither() {
		assertEquals(Success(3), Validation.fromEither(Either.Right(3)))
		assertEquals(Success(3), Either.Right(3).toValidation())
		assertEquals(Failure(5), Validation.fromEither(Either.Left(5)))
		assertEquals(Failure(5), Either.Left(5).toValidation())
	}

	@Test
	fun toEither() {
		assertEquals(Either.Right(3), Success(3).toEither())
		assertEquals(Either.Left(3), Failure(3).toEither())
	}

	@Test
	fun fromOptional() {
		assertEquals(Success(3), Validation.fromOptional(Optional.just(3)) { fail() })
		assertEquals(Success(3), Optional.just(3).toValidation { fail() })
		assertEquals(Failure(3), Validation.fromOptional(Optional.empty<Int>()) { 3 })
		assertEquals(Failure(3), Optional.empty<Int>().toValidation { 3 })
	}

	@Test
	fun toOptional() {
		assertEquals(Optional.just(3), Success(3).toOptional())
		assertEquals(Optional.empty<Int>(), Failure(3).toOptional())
	}

	@Test
	fun withEither() {
		assertEquals(Success(3), Success(3).withEither { it })
		assertEquals(
			Failure("5"),
			Success(3).withEither { validation ->
				validation.flatMap { Either.Left("${it + 2}") }
			})
		assertEquals(
			Failure("3"),
			Failure(3).withEither { validation ->
				validation.mapLeft { "$it" }
			})
		assertEquals(Success(3), Failure(3).withEither { it.swap() })
	}

	@Test
	fun toValidationNel() {
		assertEquals(Success(3), Success(3).toValidationNel())
		assertEquals(Failure(nelOf(5)), Failure(5).toValidationNel())
		assertEquals(Success(3), Either.Right(3).toValidationNel())
		assertEquals(Failure(nelOf(5)), Either.Left(5).toValidationNel())
	}

	@Test
	fun swap() {
		assertEquals(Failure("5"), Success("5").swap())
		assertEquals(Success("5"), Failure("5").swap())
	}

	@Test
	fun liftError() {
		assertEquals(Success(3), Validation.liftError(Either.Right(3)) { fail() })
		assertEquals(Failure("32"), Validation.liftError(Either.Left(3)) { "$it" + 2 })
	}

	@Test
	fun orElse() {
		assertEquals(3, Success(3).orElse { fail() })
		assertEquals(5, Failure("3") orElse 5)
	}

	@Test
	fun valueOr() {
		assertEquals(3, Success(3).valueOr { fail() })
		assertEquals("35", Failure("3").valueOr { it + 5 })
	}

	@Test
	fun ensure() {
		assertEquals(Success(4), Success(3).ensure("5") { Optional.just(it + 1) })
		assertEquals(Failure("5"), Success(3).ensure("5") { Optional.empty<Nothing>() })
		assertEquals(Failure("3"), (Failure("3") as Validation<String, Int>).ensure("5") { Optional.just(it + 1) })
	}

	@Test
	fun codiagonal() {
		assertEquals(3, Success(3).codiagonal())
		assertEquals(4, Failure(4).codiagonal())
	}

	@Test
	fun bindValidation() {
		assertEquals(Failure(3), Failure(3).bindValidation { Success("$it a") })
		assertEquals(Success("5 a"), Success(5).bindValidation { Success("$it a") })
		assertEquals(Failure(3), Failure(3).bindValidation { Failure("$it a") })
		assertEquals(Failure("5 a"), Success(5).bindValidation { Failure("$it a") })
	}
}