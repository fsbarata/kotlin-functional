package com.github.fsbarata.functional.data.validation

import com.github.fsbarata.functional.data.monoid.sumIntMonoid
import com.github.fsbarata.functional.data.validation.Validation.Failure
import com.github.fsbarata.functional.data.validation.Validation.Success
import org.junit.Assert.assertEquals
import org.junit.Test

class ValidationApplicativeTest {


	@Test
	fun ap() {
		ValidationApplicative(sumIntMonoid()).run {
			assertEquals(Success("31"), ap(Success("3"), Success { a: String -> a + 1 }))
			assertEquals(Failure(3), ap(Failure(3), Success { a: String -> a + 1 }))
			assertEquals(Failure(5), ap(Failure(3), Failure(2) as Validation<Int, (String) -> Long>))
			assertEquals(Failure(2), ap(Success("3"), Failure(2) as Validation<Int, (String) -> Long>))
		}
	}

	@Suppress("UNREACHABLE_CODE")
	@Test
	fun lift2() {
		ValidationApplicative(sumIntMonoid()).run {
			assertEquals(Success("35"), lift2(Success("3"), Success(5)) { a, b -> a + b })
			assertEquals(Failure(3), lift2(Success("3"), Failure(3)) { a, b -> a + b })
			assertEquals(Failure(5), lift2(Failure(2) as Validation<Int, String>, Failure(3)) { a, b -> a + b })
			assertEquals(Failure(2), lift2(Failure(2) as Validation<Int, String>, Success(1)) { a, b -> a + b })
		}
	}
}