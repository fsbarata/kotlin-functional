package com.github.fsbarata.functional.data.test

import com.github.fsbarata.functional.control.Functor
import com.github.fsbarata.functional.control.test.FunctorTest
import com.github.fsbarata.functional.data.Foldable
import com.github.fsbarata.functional.data.Traversable
import com.github.fsbarata.functional.data.identity.Identity
import com.github.fsbarata.functional.data.identity.runIdentity
import com.github.fsbarata.functional.data.list.ListF
import com.github.fsbarata.functional.data.list.asList
import com.github.fsbarata.functional.data.list.f
import com.github.fsbarata.functional.data.sequenceFromTraverse
import com.github.fsbarata.functional.data.traverseFromSequence
import org.junit.Assert.assertEquals
import org.junit.Test

interface TraversableTest<C>: FunctorTest<C>, FoldableTest {
	val traversableScope: Traversable.Scope<C>
	fun <A> createTraversable(vararg items: A): Traversable<C, A>

	override fun <A> createFunctor(a: A): Functor<C, A> = createTraversable(a)
	override fun <A> createFoldable(vararg items: A): Foldable<A> =
		createTraversable(*items)

	@Test
	fun `traverse identity`() {
		val t = createTraversable(1, 5, 2)
		val r1 = t.traverse(Identity, ::Identity)
		assertEqual(r1.runIdentity(), t)
	}

	@Test
	fun `traverse = traverseFromSequence`() {
		val t = createTraversable(1, 5, 2)
		val f = { a: Int -> listOf(a.toString(), (a + 2).toString()).f() }
		val r1 = t.traverse(ListF, f).asList
		val r2 = traverseFromSequence(ListF, t, f).asList
		assertEquals(r2.size, r1.size)
		r1.indices.forEach { assertEqual(r1[it], r2[it]) }
	}

	@Test
	fun `sequence = sequenceFromTraverse`() {
		val t = createTraversable(listOf(1, 5, 2).f())
		val r1 = traversableScope.sequenceA(ListF, t).asList
		val r2 = sequenceFromTraverse(ListF, t).asList
		assertEquals(r2.size, r1.size)
		r1.indices.forEach { assertEqual(r1[it], r2[it]) }
	}
}