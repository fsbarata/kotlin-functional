package com.github.fsbarata.functional.data.tuple

import com.github.fsbarata.functional.assertEquals
import com.github.fsbarata.functional.control.ComonadLaws
import com.github.fsbarata.functional.data.BiFunctorLaws
import com.github.fsbarata.functional.data.TraversableLaws
import com.github.fsbarata.functional.data.maybe.Optional
import kotlin.test.Test

class Tuple2Test:
	TraversableLaws<Tuple2Context<String>>,
	BiFunctorLaws<Tuple2BiContext>,
	ComonadLaws<Tuple2Context<String>> {

	override fun <B, A> createBiFunctor(a: A, b: B) = Tuple2(b, a)

	override val traversableScope = Tuple2.Scope<String>()

	override fun <A> createTraversable(vararg items: A) = Tuple2("2.0", items.first())

	override val possibilities: Int = 2
	override fun factory(possibility: Int) = Tuple2("1.5", possibility)

	@Test
	fun extract() {
		assertEquals(3, Tuple2("5", 3).extract())
	}

	@Test
	fun map() {
		assertEquals(Tuple2("5", 6), Tuple2("5", 3).map { it + 3 })
	}

	@Test
	fun mapLeft() {
		assertEquals(Tuple2("53", 3), Tuple2("5", 3).mapLeft { it + 3 })
	}

	@Test
	fun bimap() {
		assertEquals(Tuple2("52", 6), Tuple2("5", 3).bimap({ it + 2 }) { it + 3 })
	}

	@Test
	fun coflatMap() {
		assertEquals(Tuple2("5", "532"), Tuple2("5", 3).coflatMap { it.x + it.y + 2 })
	}

	@Test
	fun duplicate() {
		assertEquals(Tuple2("5", Tuple2("5", 3)), Tuple2("5", 3).duplicate())
	}

	@Test
	fun swap() {
		assertEquals(Tuple2(3, "5"), Tuple2("5", 3).swap())
	}

	@Test
	fun traverse() {
		assertEquals(
			Optional.just(Tuple2("5", 4.2)),
			Tuple2("5", 3).traverse(Optional) { Optional.just(it + 1.2) })

		assertEquals(
			Optional.empty<Tuple2<String, Double>>(),
			Tuple2("5", 3).traverse(Optional) { Optional.empty<Double>() })
	}

	@Test
	fun toPair() {
		assertEquals("5" to 3, Tuple2("5", 3).toPair())
	}
}
