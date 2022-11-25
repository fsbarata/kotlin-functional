package com.github.fsbarata.functional.data.or

import com.github.fsbarata.functional.PossibilitiesTest
import com.github.fsbarata.functional.assertEquals
import com.github.fsbarata.functional.control.MonadScopeLaws
import com.github.fsbarata.functional.data.BiFunctorLaws
import com.github.fsbarata.functional.data.TraversableLaws
import kotlin.test.Test
import kotlin.test.fail

abstract class OrPossibilities: PossibilitiesTest {
	val left = "a left item"
	override val possibilities: Int = 5
	override fun factory(possibility: Int) = when (possibility) {
		0 -> Or.Left(left)
		1, 2 -> Or.Right(possibility - 1)
		else -> Or.Both(left, possibility - 1)
	}
}

class OrTest: OrPossibilities(),
	TraversableLaws<OrContext<String>>,
	BiFunctorLaws<OrBiContext> {
	override val functorScope = Or.Scope<String>()
	override val traversableScope = Or.Scope<String>()

	override fun <A> createTraversable(vararg items: A): Or<String, A> =
		when (items.size) {
			0 -> Or.Left(left)
			1 -> Or.Right(items[0])
			else -> Or.Both(left, items[0])
		}

	override fun <B, A> createBiFunctor(a: A, b: B) =
		if (b == null) Or.Right(a) else Or.Left(b)

	@Test
	fun map() {
		assertEquals(LEFT, LEFT.map { it * 2 })
		assertEquals(Or.Right(10), RIGHT.map { it * 2 })
		assertEquals(Or.Right("5 a"), RIGHT.map { "$it a" })
		assertEquals(Or.Both("5", "7 a"), BOTH.map { "$it a" })
	}

	@Test
	fun flatMap() {
		assertEquals(LEFT, LEFT.flatMap(String::plus) { Or.Left("$it a") })
		assertEquals(LEFT, LEFT.flatMap(String::plus) { Or.right("$it a") })
		assertEquals(LEFT, LEFT.flatMap(String::plus) { Or.Both("v", "$it a") })
		assertEquals(Or.Left("5 a"), RIGHT.flatMap(String::plus) { Or.Left("$it a") })
		assertEquals(Or.Right("5 a"), RIGHT.flatMap(String::plus) { Or.right("$it a") })
		assertEquals(Or.Both("5 a", "5 b"), RIGHT.flatMap(String::plus) { Or.Both("$it a", "$it b") })
		assertEquals(Or.Left("57 a"), BOTH.flatMap(String::plus) { Or.Left("$it a") })
		assertEquals(Or.Both("5", "7 a"), BOTH.flatMap(String::plus) { Or.right("$it a") })
		assertEquals(Or.Both("57 a", "7 b"), BOTH.flatMap(String::plus) { Or.Both("$it a", "$it b") })
	}

	@Test
	fun mapLeft() {
		assertEquals(Or.Left("52"), LEFT.mapLeft { it + 2 })
		assertEquals(Or.Left(5), LEFT.mapLeft { it.toInt() })
		assertEquals(RIGHT, RIGHT.mapLeft { it + 2 })
		assertEquals(Or.Both("52", 7), BOTH.mapLeft { it + 2 })
	}

	@Test
	fun bimap() {
		assertEquals(Or.Left("52"), LEFT.bimap({ it + 2 }, { it + 2 }))
		assertEquals(Or.Left(5), LEFT.bimap({ it.toInt() }, { it + 2 }))
		assertEquals(Or.Right(7), RIGHT.bimap({ it + 2 }, { it + 2 }))
		assertEquals(Or.Right("5 + 2"), RIGHT.bimap({ it + 2 }, { "$it + 2" }))
		assertEquals(Or.Both("52", 9), BOTH.bimap({ it + 2 }, { it + 2 }))
	}

	@Test
	fun fold() {
		assertEquals(
			"5 a",
			LEFT.fold(ifLeft = { "$it a" }, ifRight = { "$it b" }, ifBoth = { left, right -> "$left $right ab" }),
		)
		assertEquals(
			"5 b",
			RIGHT.fold(ifLeft = { "$it a" }, ifRight = { "$it b" }, ifBoth = { left, right -> "$left $right ab" }),
		)
		assertEquals(
			"5 7 ab",
			BOTH.fold(ifLeft = { "$it a" }, ifRight = { "$it b" }, ifBoth = { left, right -> "$left $right ab" }),
		)
		LEFT.fold(ifLeft = {}, ifRight = { fail() }, ifBoth = { _, _ -> fail() })
		RIGHT.fold(ifLeft = { fail() }, ifRight = {}, ifBoth = { _, _ -> fail() })
		BOTH.fold(ifLeft = { fail() }, ifRight = { fail() }, ifBoth = { _, _ -> })
	}

	@Test
	fun swap() {
		assertEquals(Or.Right("5"), LEFT.swap())
		assertEquals(Or.Left(5), RIGHT.swap())
		assertEquals(Or.Both(7, "5"), BOTH.swap())
	}

	companion object {
		private val LEFT: Or<String, Int> = Or.Left("5")
		private val RIGHT: Or<String, Int> = Or.Right(5)
		private val BOTH: Or<String, Int> = Or.Both("5", 7)
	}
}

class OrMonadTest: OrPossibilities(), MonadScopeLaws<OrContext<String>> {
	override val monadScope = Or.MonadScope(String::plus)
}
