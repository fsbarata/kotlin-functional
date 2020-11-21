package com.github.fsbarata.functional.data

import com.github.fsbarata.functional.data.BiFunctor
import com.github.fsbarata.functional.data.compose
import com.github.fsbarata.functional.data.id
import org.junit.Test

interface BiFunctorLaws<P> {
	fun <B, A> createBiFunctor(a: A, b: B): BiFunctor<P, B, A>

	fun <B, A> assertEqual(p1: BiFunctor<P, B, A>, p2: BiFunctor<P, B, A>) =
		assert(p1 == p2) { "$p1 should be equal to $p2" }

	@Test
	fun `bimap identity`() {
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
		val a = createBiFunctor("a", 3)
		val r1 = a.map(id())
		assertEqual(a, r1)

		val b = createBiFunctor(null, 4)
		val r2 = b.map(id())
		assertEqual(b, r2)
	}

	@Test
	fun `mapLeft identity`() {
		val a = createBiFunctor("a", 3)
		val r1 = a.mapLeft(id())
		assertEqual(a, r1)

		val b = createBiFunctor(4, null)
		val r2 = b.mapLeft(id())
		assertEqual(b, r2)
	}

	@Test
	fun `bimap equivalence`() {
		val f = { b: Int? -> b?.plus(2) }
		val g = { a: String? -> a?.plus("gg") }
		val a = createBiFunctor("a", 3)
		assertEqual(a.bimap(f, g), a.map(g).mapLeft(f))

		val b = createBiFunctor("b", null)
		assertEqual(b.bimap(f, g), b.map(g).mapLeft(f))

		val c = createBiFunctor(null, 3)
		assertEqual(c.bimap(f, g), c.map(g).mapLeft(f))
	}

	@Test
	fun `bimap composition`() {
		val f = { b: Int? -> b?.plus(2) }
		val h = { b: Int? -> b?.times(2) }
		val g = { a: String? -> a?.plus("gg") }
		val i = { a: String? -> a?.plus("we") }

		val a = createBiFunctor("a", 3)
		assertEqual(a.bimap(h, i).bimap(f, g), a.bimap(f compose h, g compose i))

		val b = createBiFunctor("b", null)
		assertEqual(b.bimap(h, i).bimap(f, g), b.bimap(f compose h, g compose i))

		val c = createBiFunctor(null, 3)
		assertEqual(c.bimap(h, i).bimap(f, g), c.bimap(f compose h, g compose i))
	}
}