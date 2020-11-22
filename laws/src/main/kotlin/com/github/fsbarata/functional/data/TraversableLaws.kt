package com.github.fsbarata.functional.data

import com.github.fsbarata.functional.data.compose.Composite
import com.github.fsbarata.functional.data.compose.CompositeApplicative
import com.github.fsbarata.functional.data.compose.asCompose
import com.github.fsbarata.functional.data.identity.Identity
import com.github.fsbarata.functional.data.identity.runIdentity
import com.github.fsbarata.functional.data.list.ListF
import com.github.fsbarata.functional.data.list.asList
import com.github.fsbarata.functional.data.list.f
import com.github.fsbarata.functional.data.maybe.Optional
import com.github.fsbarata.functional.data.maybe.asOptional
import org.junit.Assert.assertEquals
import org.junit.Test

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
			t.traverse(CompositeApplicative.Scope(ListF, Optional)) { a -> CompositeApplicative(f(a).map(g), Optional) }
		val r2 = Composite(t.traverse(ListF, f).map { it.traverse(Optional, g) })
		val r1Items = r1.asCompose.fg.asList
		val r2Items = r2.fg.asList
		assertEquals(r2Items.size, r1Items.size)
		r1Items.zipWith(r2Items) { optional1, optional2 ->
			val item1 = optional1.asOptional.orNull() ?: run {
				assert(optional2.asOptional.isPresent()) { "Item from r2 is not null" }
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
		val r2 = traverseFromSequence(ListF, t, f).asList
		assertEquals(r2.size, r1.size)
		r1.indices.forEach { assertEqualF(r1[it], r2[it]) }
	}

	@Test
	fun `sequence = sequenceFromTraverse`() {
		val t = createTraversable(listOf(1, 5, 2, -2, 0, 2).f())
		val r1 = traversableScope.sequenceA(ListF, t).asList
		val r2 = sequenceFromTraverse(ListF, t).asList
		assertEquals(r2.size, r1.size)
		r1.indices.forEach { assertEqualF(r1[it], r2[it]) }
	}
}