package com.fsbarata.fp.types

import com.fsbarata.fp.concepts.Functor
import com.fsbarata.fp.concepts.test.FunctorTest
import org.junit.Assert.assertEquals
import org.junit.Assert.fail
import org.junit.Test

class ValidationTest: FunctorTest<Validation<Nothing, *>> {
	override fun <A> createFunctor(a: A): Validation<Nothing, A> = Validation.Success(a)

	override fun Functor<Validation<Nothing, *>, Int>.equalTo(other: Functor<Validation<Nothing, *>, Int>) =
		asValidation == other.asValidation

	@Test
	fun fold() {
		assertEquals("3", Validation.Success(3).fold(ifFailure = { fail() }, ifSuccess = { "$it" }))
		assertEquals(5, Validation.Failure(3).fold(ifFailure = { it + 2 }, ifSuccess = { fail() }))
	}

	@Test
	fun map() {
		assertEquals(Validation.Success("3"), Validation.Success(3).map { "$it" })
		assertEquals(Validation.Failure(3), Validation.Failure(3).map { fail() })
	}

	@Test
	fun fromEither() {
		assertEquals(Validation.Success(3), Validation.fromEither(Either.Right(3)))
		assertEquals(Validation.Success(3), Either.Right(3).toValidation())
		assertEquals(Validation.Failure(5), Validation.fromEither(Either.Left(5)))
		assertEquals(Validation.Failure(5), Either.Left(5).toValidation())
	}

	@Test
	fun toEither() {
		assertEquals(Either.Right(3), Validation.Success(3).toEither())
		assertEquals(Either.Left(3), Validation.Failure(3).toEither())
	}

	@Test
	fun fromOptional() {
		assertEquals(Validation.Success(3), Validation.fromOptional(Optional.just(3)) { fail() })
		assertEquals(Validation.Success(3), Optional.just(3).toValidation { fail() })
		assertEquals(Validation.Failure(3), Validation.fromOptional(Optional.empty<Int>()) { 3 })
		assertEquals(Validation.Failure(3), Optional.empty<Int>().toValidation { 3 })
	}

	@Test
	fun toOptional() {
		assertEquals(Optional.just(3), Validation.Success(3).toOptional())
		assertEquals(Optional.empty<Int>(), Validation.Failure(3).toOptional())
	}

	@Test
	fun withEither() {
		assertEquals(Validation.Success(3), Validation.Success(3).withEither { it })
		assertEquals(Validation.Failure("5"),
					 Validation.Success(3).withEither { it.flatMap { Either.Left("${it + 2}") } })
		assertEquals(Validation.Failure("3"),
					 Validation.Failure(3).withEither { it.mapLeft { "$it" } })
		assertEquals(Validation.Success(3), Validation.Failure(3).withEither { it.swap() })
	}

	@Test
	fun toValidationNel() {
		assertEquals(Validation.Success(3), Validation.Success(3).toValidationNel())
		assertEquals(Validation.Failure(nelOf(5)), Validation.Failure(5).toValidationNel())
		assertEquals(Validation.Success(3), Either.Right(3).toValidationNel())
		assertEquals(Validation.Failure(nelOf(5)), Either.Left(5).toValidationNel())
	}

	@Test
	fun liftError() {
		assertEquals(Validation.Success(3), Validation.liftError(Either.Right(3)) { fail() })
		assertEquals(Validation.Failure("32"), Validation.liftError(Either.Left(3)) { "$it" + 2 })
	}

	@Test
	fun orElse() {
		assertEquals(3, Validation.Success(3).orElse { fail() })
		assertEquals(5, Validation.Failure("3").orElse { 5 })
	}

	@Test
	fun valueOr() {
		assertEquals(3, Validation.Success(3).valueOr { fail() })
		assertEquals("35", Validation.Failure("3").valueOr { it + 5 })
	}
}