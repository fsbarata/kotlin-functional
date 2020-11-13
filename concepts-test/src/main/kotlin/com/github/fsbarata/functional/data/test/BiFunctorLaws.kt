package com.github.fsbarata.functional.data.test

import com.github.fsbarata.functional.data.BiFunctor
import com.github.fsbarata.functional.data.id
import org.junit.Test

interface BiFunctorLaws<P> {
	fun <B, A> createBiFunctor(a: A, b: B): BiFunctor<P, B, A>

	@Test
	fun `bimap identity`() {
		val a = createBiFunctor("a", 3)
		val r1 = a.bimap(id(), id())
		assert(a == r1) { "$a should be equal to $r1" }

		val b = createBiFunctor("b", null)
		val r2 = b.bimap(id(), id())
		assert(b == r2) { "$b should be equal to $r2" }

		val c = createBiFunctor(null, 3)
		val r3 = c.bimap(id(), id())
		assert(c == r3) { "$c should be equal to $r3" }
	}

	@Test
	fun `bifunctor map identity`() {
		val a = createBiFunctor("a", 3)
		val r1 = a.map(id())
		assert(a == r1) { "$a should be equal to $r1" }

		val b = createBiFunctor(null, 4)
		val r2 = b.map(id())
		assert(b == r2) { "$b should be equal to $r2" }
	}

	@Test
	fun `mapLeft identity`() {
		val a = createBiFunctor("a", 3)
		val r1 = a.mapLeft(id())
		assert(a == r1) { "$a should be equal to $r1" }

		val b = createBiFunctor(4, null)
		val r2 = b.mapLeft(id())
		assert(b == r2) { "$b should be equal to $r2" }
	}
}