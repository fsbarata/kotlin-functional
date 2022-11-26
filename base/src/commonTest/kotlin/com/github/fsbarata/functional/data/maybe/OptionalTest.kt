package com.github.fsbarata.functional.data.maybe

import com.github.fsbarata.functional.Context
import com.github.fsbarata.functional.assertEquals
import com.github.fsbarata.functional.control.MonadPlusLaws
import com.github.fsbarata.functional.control.MonadZipLaws
import com.github.fsbarata.functional.control.arrow.kleisli
import com.github.fsbarata.functional.data.TraversableLaws
import kotlin.test.*

class OptionalTest:
	MonadPlusLaws<OptionalContext>,
	MonadZipLaws<OptionalContext>,
	TraversableLaws<OptionalContext> {
	override val traversableScope = Optional
	override val monadScope = Optional

	override fun <A> createTraversable(vararg items: A) = Optional.ofNullable(items.firstOrNull())

	override fun <A> Context<OptionalContext, A>.equalTo(other: Context<OptionalContext, A>): Boolean =
		asOptional == other.asOptional

	override val possibilities: Int = 3
	override fun factory(possibility: Int) = when (possibility) {
		0 -> Optional.empty()
		else -> Optional.just(possibility - 1)
	}

	@Test
	fun isPresent() {
		assertTrue(Some(3).isPresent())
		assertTrue(Optional.just(3).isPresent())
		assertFalse(None.isPresent())
		assertFalse(Optional.empty<Int>().isPresent())
	}

	@Test
	fun orNull() {
		assertEquals(3, Optional.just(3).orNull())
		assertEquals("51", Some("51").orNull())
		assertNull(None.orNull())
		assertNull(Optional.empty<Int>().orNull())
	}

	@Test
	fun fold() {
		assertEquals(3, Optional.just(9).fold(ifEmpty = { fail() }, { it / 3 }))
		assertEquals("59", Optional.just(9).fold(ifEmpty = { "N" }, { "5$it" }))
		assertEquals("N", Optional.empty<Int>().fold(ifEmpty = { "N" }, { fail() }))
	}

	@Test
	fun filter() {
		assertEquals(Optional.just(9), Optional.just(9).filter { true })
		assertEquals(Optional.empty<Int>(), Optional.just(9).filter { false })
		assertEquals(Optional.empty<Int>(), Optional.empty<Int>().filter { true })
		assertEquals(Optional.empty<Int>(), Optional.empty<Int>().filter { false })
	}

	@Test
	fun map() {
		assertEquals(Optional.just(45L), Optional.just(9).map { 5L * it })
		assertEquals(Optional.empty<Int>(), Optional.empty<Int>().map { 5L * it })
	}

	@Test
	fun flatMap() {
		assertEquals(Optional.just(45L), Optional.just(9).flatMap { Optional.just(5L * it) })
		assertEquals(Optional.empty<Long>(), Optional.just(9).flatMap { Optional.empty<Long>() })
		assertEquals(Optional.empty<Long>(), Optional.empty<Int>().flatMap { Optional.just(5L * it) })
		assertEquals(Optional.empty<Long>(), Optional.empty<Int>().flatMap { Optional.empty<Long>() })
	}

	@Test
	fun kleisli() {
		val m = Optional.just(3)
		val f = optionalKleisli { b: Int -> "$b" }
		assertEquals(Optional.just("3"), m.bind(f))
		val g = optionalKleisli { _: Int -> null }
		assertEquals(Optional.empty<Nothing>(), m.bind(g))
	}
}
