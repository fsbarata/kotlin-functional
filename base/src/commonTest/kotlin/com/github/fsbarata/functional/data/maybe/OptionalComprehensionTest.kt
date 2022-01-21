package com.github.fsbarata.functional.data.maybe

import com.github.fsbarata.functional.assertEquals
import kotlin.test.Test

class OptionalComprehensionTest {
	@Test
	fun successful_both_somes() {
		val optional1 = Optional.just(3)
		val optional2 = Optional.just("5")
		assertEquals(Optional.just(8), Optional {
			val a = optional1.bind()
			val b = optional2.flatMap { it.toIntOrNull().toOptional() }.bind()
			a + b
		})
	}

	@Test
	fun failure_any_none() {
		val optional1 = Optional.just(3)
		assertEquals(None, Optional {
			val a = optional1.bind()
			val b = Optional.empty<Int>().bind()
			a + b
		})
		assertEquals(None, Optional {
			val a = Optional.empty<Int>().bind()
			val b = optional1.bind()
			a + b
		})
	}
}