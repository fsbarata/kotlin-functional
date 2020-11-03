package com.fsbarata.fp.types

import com.fsbarata.fp.concepts.Functor
import com.fsbarata.fp.concepts.test.FunctorTest
import com.fsbarata.fp.monoid.sumIntMonoid
import com.fsbarata.fp.types.Validation.Failure
import com.fsbarata.fp.types.Validation.Success
import org.junit.Assert.assertEquals
import org.junit.Assert.fail
import org.junit.Test

class ValidationTest: FunctorTest<Validation<Nothing, *>> {
	override fun <A> createFunctor(a: A): Validation<Nothing, A> = Success(a)

	override fun Functor<Validation<Nothing, *>, Int>.equalTo(other: Functor<Validation<Nothing, *>, Int>) =
		asValidation == other.asValidation

	@Test
	fun fold() {
		assertEquals("3", Success(3).fold(ifFailure = { fail() }, ifSuccess = { "$it" }))
		assertEquals(5, Failure(3).fold(ifFailure = { it + 2 }, ifSuccess = { fail() }))
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
		assertEquals(Failure("5"),
			Success(3).withEither { it.flatMap { Either.Left("${it + 2}") } })
		assertEquals(Failure("3"),
			Failure(3).withEither { it.mapLeft { "$it" } })
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

	@Test
	fun ap() {
		with(sumIntMonoid()) {
			assertEquals(Success("31"), ap(Success("3"), Success { a: String -> a + 1 }))
			assertEquals(Failure(3), ap(Failure(3), Success { a: String -> a + 1 }))
			assertEquals(Failure(5), ap(Failure(3), Failure(2) as Validation<Int, (String) -> Long>))
			assertEquals(Failure(2), ap(Success("3"), Failure(2) as Validation<Int, (String) -> Long>))
		}
	}

	@Test
	fun sequence() {
		with(sumIntMonoid()) {
			assertEquals(Success("35"), sequence(Success("3"), Success(5)) { a, b -> a + b })
			assertEquals(Failure(3), sequence(Success("3"), Failure(3)) { a, b -> a + b })
			assertEquals(Failure(5), sequence(Failure(2) as Validation<Int, String>, Failure(3)) { a, b -> a + b })
			assertEquals(Failure(2), sequence(Failure(2) as Validation<Int, String>, Success(1)) { a, b -> a + b })
		}
	}
}