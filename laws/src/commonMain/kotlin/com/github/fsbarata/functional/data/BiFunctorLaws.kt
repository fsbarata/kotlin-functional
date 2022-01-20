package com.github.fsbarata.functional.data

import com.github.fsbarata.functional.PossibilitiesTest
import kotlin.test.Test
import kotlin.test.asserter

interface BiFunctorLaws<P>: PossibilitiesTest {
	fun <B, A> createBiFunctor(a: A, b: B): BiFunctor<P, B, A>

	override fun factory(possibility: Int): BiFunctor<P, String?, Int?>

	@Suppress("UNCHECKED_CAST")
	private fun eachPossibility(block: (BiFunctor<P, String?, Int?>) -> Unit) {
		super.eachPossibility { block(it as BiFunctor<P, String?, Int?>) }
	}

	fun <B, A> assertEqual(p1: BiFunctor<P, B, A>, p2: BiFunctor<P, B, A>) =
		asserter.assertTrue({ "$p1 should be equal to $p2" }, p1 == p2)

	@Test
	fun `bimap identity`() {
		eachPossibility { a ->
			val r = a.bimap(id(), id())
			assertEqual(a, r)
		}

		val a = createBiFunctor("a", 3)
		val r1 = a.bimap(id(), id())
		assertEqual(a, r1)

		val b = createBiFunctor("b", null)
		val r2 = b.bimap(id(), id())
		assertEqual(b, r2)

		val c = createBiFunctor(null, 3)
		val r3 = c.bimap(id(), id())
		assertEqual(c, r3)
	}

	@Test
	fun `bifunctor map identity`() {
		eachPossibility { a ->
			val r = a.map(id())
			assertEqual(a, r)
		}

		val b = createBiFunctor(null, 4)
		val r2 = b.map(id())
		assertEqual(b, r2)
	}

	@Test
	fun `mapLeft identity`() {
		eachPossibility { a ->
			val r = a.mapLeft(id())
			assertEqual(a, r)
		}

		val b = createBiFunctor(4, null)
		val r2 = b.mapLeft(id())
		assertEqual(b, r2)
	}

	@Test
	fun `bimap equivalence`() {
		eachPossibility { v ->
			val f = { a: String? -> a?.plus("gg") }
			val g = { b: Int? -> b?.plus(2) }
			assertEqual(v.bimap(f, g), v.map(g).mapLeft(f))
		}
	}

	@Test
	fun `bimap composition`() {
		eachPossibility { v ->
			val f = { a: String? -> a?.plus("gg") }
			val g = { b: Int? -> b?.plus(2) }
			val h = { a: String? -> a?.plus("we") }
			val i = { b: Int? -> b?.times(2) }

			assertEqual(v.bimap(h, i).bimap(f, g), v.bimap(f compose h, g compose i))
		}
	}
}