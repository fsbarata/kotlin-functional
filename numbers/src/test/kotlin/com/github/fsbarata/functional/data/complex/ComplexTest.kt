package com.github.fsbarata.functional.data.complex

import com.github.fsbarata.functional.control.MonadLaws
import com.github.fsbarata.functional.data.TraversableLaws
import org.junit.Assert.assertEquals
import org.junit.Test

class ComplexTest: MonadLaws<ComplexContext>, TraversableLaws<ComplexContext> {
	override val monadScope = Complex
	override val traversableScope = Complex

	override val possibilities: Int = 5

	override fun factory(possibility: Int) = Complex(possibility, possibility % 3)

	override fun <A> createTraversable(vararg items: A) =
		Complex(items[0], items.getOrNull(1) ?: items[0])

	@Test
	fun conjugate() {
		assertEquals(
			Complex(1, 3),
			Complex(1, -3).conjugate()
		)

		assertEquals(
			Complex(1.2f, -3f),
			Complex(1.2f, 3f).conjugate()
		)

		assertEquals(
			Complex(-1.2, -3.5),
			Complex(-1.2, 3.5).conjugate()
		)
	}

	@Test
	fun magnitude() {
		assertEquals(
			5.0,
			Complex(3, 4).magnitude(),
			1e-10
		)

		assertEquals(
			5.0,
			Complex(3f, 4f).magnitude(),
			1e-10
		)

		assertEquals(
			5.0,
			Complex(3.0, 4.0).magnitude(),
			1e-10
		)
	}

	@Test
	fun phase() {
		assertEquals(
			5.0,
			Complex(3, 4).magnitude(),
			1e-10
		)

		assertEquals(
			5.0,
			Complex(3f, 4f).magnitude(),
			1e-10
		)

		assertEquals(
			5.0,
			Complex(3.0, 4.0).magnitude(),
			1e-10
		)
	}
}