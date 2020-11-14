package com.github.fsbarata.functional.data.validation

import com.github.fsbarata.functional.data.Semigroup
import com.github.fsbarata.functional.data.validation.Validation.Failure
import com.github.fsbarata.functional.data.validation.Validation.Success
import org.junit.Assert.assertEquals
import org.junit.Test

class ValidationApplicativeTest {
	data class IntSemigroup(val i: Int): Semigroup<IntSemigroup> {
		override fun combineWith(other: IntSemigroup) = IntSemigroup(i + other.i)
	}

	@Test
	fun ap() {
		ValidationApplicative<IntSemigroup>().run {
			assertEquals(Success("31"), ap(Success("3"), Success { a: String -> a + 1 }))
			assertEquals(Failure(IntSemigroup(3)), ap(Failure(IntSemigroup(3)), Success { a: String -> a + 1 }))
			assertEquals(Failure(IntSemigroup(5)), ap(Failure(IntSemigroup(3)), Failure(IntSemigroup(2)) as Validation<IntSemigroup, (String) -> Long>))
			assertEquals(Failure(IntSemigroup(2)), ap(Success("3"), Failure(IntSemigroup(2)) as Validation<IntSemigroup, (String) -> Long>))
		}
	}

	@Suppress("UNREACHABLE_CODE")
	@Test
	fun lift2() {
		ValidationApplicative<IntSemigroup>().run {
			assertEquals(Success("35"), lift2(Success("3"), Success(5)) { a, b -> a + b })
			assertEquals(Failure(IntSemigroup(3)), lift2(Success("3"), Failure(IntSemigroup(3))) { a, b -> a + b })
			assertEquals(Failure(IntSemigroup(5)), lift2(Failure(IntSemigroup(2)) as Validation<IntSemigroup, String>, Failure(IntSemigroup(3))) { a, b -> a + b })
			assertEquals(Failure(IntSemigroup(2)), lift2(Failure(IntSemigroup(2)) as Validation<IntSemigroup, String>, Success(1)) { a, b -> a + b })
		}
	}
}