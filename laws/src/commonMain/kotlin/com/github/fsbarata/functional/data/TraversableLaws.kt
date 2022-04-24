package com.github.fsbarata.functional.data

import com.github.fsbarata.functional.data.compose.ComposedApplicative
import com.github.fsbarata.functional.data.compose.asComposed
import com.github.fsbarata.functional.data.identity.Identity
import com.github.fsbarata.functional.data.identity.runIdentity
import com.github.fsbarata.functional.data.list.ListF
import com.github.fsbarata.functional.data.list.asList
import com.github.fsbarata.functional.data.list.f
import com.github.fsbarata.functional.data.maybe.Optional
import com.github.fsbarata.functional.data.maybe.asOptional
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

interface TraversableLaws<T>: FunctorLaws<T>, FoldableLaws {
	val traversableScope: Traversable.Scope<T>
	fun <A> createTraversable(vararg items: A): Traversable<T, A>

	override fun <A> createFoldable(vararg items: A): Foldable<A> =
		createTraversable(*items)

	@Test
	fun `traverse identity`() {
		val t = createTraversable(1, 5, 2, -2, 0, 2)
		val r1 = t.traverse(Identity, ::Identity)
		assertEqualF(r1.runIdentity(), t)
	}

	@Test
	fun `traverse composition`() {
		val t = createTraversable(1, 5, 2, -2, 0, 2)
		val f = { a: Int -> ListF.of(a - 5, a - 1) }
		val g = { a: Int -> Optional.just(a + 3) }

		val r1 =
			t.traverse(ComposedApplicative.Scope(ListF, Optional)) { a ->
				ComposedApplicative(
					f(a).map(g),
					ListF,
					Optional)
			}
		val r2 = t.traverse(ListF, f).asList.map { it.traverse(Optional, g) }
		val r1Items = r1.asComposed.underlying.asList
		val r2Items = r2
		assertEquals(r2Items.size, r1Items.size)
		r1Items.zipWith(r2Items) { optional1, optional2 ->
			val item1 = optional1.asOptional.orNull() ?: run {
				assertTrue(optional2.asOptional.isPresent(), message = "Item from r2 is not null")
				return@zipWith
			}
			val item2 = checkNotNull(optional2.asOptional.orNull())
			assertEqualF(item1, item2)
		}
	}

	@Test
	fun `traverse = traverseFromSequence`() {
		val t = createTraversable(1, 5, 2, -2, 0, 2)
		val f = { a: Int -> listOf(a.toString(), (a + 2).toString()).f() }
		val r1 = t.traverse(ListF, f).asList
		val r2 = traverseFromSequence(traversableScope, t, ListF, f).asList
		assertEquals(r2.size, r1.size)
		r1.indices.forEach { assertEqualF(r1[it], r2[it]) }
	}

	@Test
	fun `sequence = sequenceFromTraverse`() {
		val t = createTraversable(listOf(1, 5, 2, -2, 0, 2).f())
		val r1 = traversableScope.sequenceA(ListF, t).asList
		val r2 = sequenceFromTraverse(traversableScope, t, ListF).asList
		assertEquals(r2.size, r1.size)
		r1.indices.forEach { assertEqualF(r1[it], r2[it]) }
	}
}