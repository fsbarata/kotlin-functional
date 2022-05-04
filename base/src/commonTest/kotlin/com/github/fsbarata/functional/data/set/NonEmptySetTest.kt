package com.github.fsbarata.functional.data.set

import com.github.fsbarata.functional.assertEquals
import com.github.fsbarata.functional.control.ComonadLaws
import com.github.fsbarata.functional.control.MonadLaws
import com.github.fsbarata.functional.control.flatten
import com.github.fsbarata.functional.data.TraversableLaws
import com.github.fsbarata.functional.data.collection.max
import com.github.fsbarata.functional.data.collection.min
import com.github.fsbarata.functional.data.list.createNel
import com.github.fsbarata.functional.data.list.nelOf
import kotlin.test.Test

class NonEmptySetTest:
	MonadLaws<NonEmptySetContext>,
	TraversableLaws<NonEmptySetContext>,
	ComonadLaws<NonEmptySetContext> {
	override val monadScope = NonEmptySet
	override val traversableScope = NonEmptySet

	override fun <A> createTraversable(vararg items: A) =
		items.toSet().toNes() ?: throw NoSuchElementException()

	override val possibilities = 10
	override fun factory(possibility: Int) = createNel(possibility).toNes()

	private val nes1 = NonEmptySet.just(9)
	private val nes2 = nesOf(5, 1, 3)
	private val nes3 = NonEmptySet.of(2, nesOf(4, 2, 5))

	@Test
	fun size() {
		assertEquals(1, nes1.size)
		assertEquals(3, nes2.size)
		assertEquals(3, nes3.size)
	}

	@Test
	fun first() {
		assertEquals(9, nes1.first())
		assertEquals(5, nes2.first())
		assertEquals(2, nes3.first())
	}

	@Test
	fun last() {
		assertEquals(9, nes1.last())
		assertEquals(3, nes2.last())
		assertEquals(5, nes3.last())
	}

	@Test
	fun equals() {
		assertEquals(nesOf(9), nes1)
		assertEquals(nesOf(1, 3, 5), nes2)
		assertEquals(nesOf(2, 4, 5), nes3)
	}

	@Test
	fun iterable() {
		assertEquals(setOf(9), Iterable { nes1.iterator() }.toSet())
		assertEquals(setOf(5, 1, 3), Iterable { nes2.iterator() }.toSet())
		assertEquals(setOf(2, 4, 5), Iterable { nes3.iterator() }.toSet())
	}

	@Test
	fun map() {
		assertEquals(NonEmptySet.just(45), nes1.map { it * 5 })
		assertEquals(nesOf(25, 5, 15), nes2.map { it * 5 })
		assertEquals(nesOf(10, 20, 10, 25), nes3.map { it * 5 })
	}

	@Test
	fun flatMap() {
		assertEquals(nesOf(90, 9), nes1.flatMap { nesOf(10 * it, it) })
		assertEquals(nesOf(50, 5, 10, 1, 30, 3), nes2.flatMap { nesOf(10 * it, it) })
		assertEquals(nesOf(20, 2, 40, 4, 50, 5), nes3.flatMap { nesOf(10 * it, it) })
	}

	@Test
	fun fold() {
		assertEquals(45L, nes1.foldL(5L, Long::times))
		assertEquals(30L, nes2.foldL(2L, Long::times))
		assertEquals(120L, nes3.foldL(3L, Long::times))
	}

	@Test
	fun plus() {
		assertEquals(nesOf(9, 3), nes1 + 3)
		assertEquals(2, (nes1 + 3).size)
		assertEquals(nesOf(5, 1, 3), nes2 + 3)
		assertEquals(3, (nes2 + 3).size)
		assertEquals(nesOf(2, 4, 5, 3), nes3 + 3)
		assertEquals(4, (nes3 + 3).size)

		assertEquals(nes1, nes1 + emptySet())
		assertEquals(nes2, nes2 + emptySet())
		assertEquals(nes3, nes3 + emptySet())

		assertEquals(nesOf(9, 5), nes1 + setOf(5, 5))
		assertEquals(2, (nes1 + setOf(5, 5)).size)
		assertEquals(nesOf(5, 1, 3, 2, 6), nes2 + setOf(1, 2, 6))
		assertEquals(5, (nes2 + setOf(1, 2, 6)).size)
		assertEquals(nesOf(2, 4, 5, 3), nes3 + setOf(3))
		assertEquals(4, (nes3 + setOf(3)).size)
	}

	@Test
	fun max() {
		assertEquals(9, nes1.max())
		assertEquals(5, nes2.max())
		assertEquals(5, nes3.max())
	}

	@Test
	fun min() {
		assertEquals(9, nes1.min())
		assertEquals(1, nes2.min())
		assertEquals(2, nes3.min())
	}

	@Test
	fun maxBy() {
		assertEquals(9, nes1.maxBy { it })
		assertEquals(5, nes2.maxBy { it % 3L })
		assertEquals(4, nes3.maxBy { it % 5L })
	}

	@Test
	fun maxOf() {
		assertEquals(9L, nes1.maxOf { it.toLong() })
		assertEquals(2.5, nes2.maxOf { (it % 3L) + 0.5 })
		assertEquals(4, nes3.maxOf { it % 5 })
	}

	@Test
	fun maxWith() {
		assertEquals(9, nes1.maxWith(compareBy { it % 3 }))
		assertEquals(5, nes2.maxWith(compareBy { it % 3 }))
		assertEquals(2, nes3.maxWith(compareBy { -it }))
	}

	@Test
	fun minBy() {
		assertEquals(9, nes1.minBy { it })
		assertEquals(3, nes2.minBy { it % 3L })
		assertEquals(5, nes3.minBy { it % 5L })
	}

	@Test
	fun minOf() {
		assertEquals(9L, nes1.minOf { it.toLong() })
		assertEquals(0.5, nes2.minOf { (it % 3L) + 0.5 })
		assertEquals(0, nes3.minOf { it % 5 })
	}

	@Test
	fun minWith() {
		assertEquals(9, nes1.minWith(compareBy { it % 3 }))
		assertEquals(3, nes2.minWith(compareBy { it % 3 }))
		assertEquals(2, nes3.minWith(compareBy { it }))
	}

	@Test
	fun union() {
		assertEquals(nesOf(9, 5, 1, 3), nes1.union(nes2))
		assertEquals(nesOf(2, 4, 5, 1, 3), nes3.union(nes2))
		assertEquals(nesOf(3, 5, 2, 4), nesOf(3, 5, 2).union(nelOf(4, 3, 2)))
	}

	@Test
	fun flatten() {
		assertEquals(
			nesOf(3, 5, 1, 9),
			NonEmptySet.of(
				nesOf(3, 5),
				setOf(
					nesOf(1, 3),
					NonEmptySet.just(9)
				)
			).flatten()
		)
	}

	@Test
	fun asSequence() {
		assertEquals(nes1, nes1.asSequence().toSetF())
		assertEquals(nes2, nes2.asSequence().toSetF())
		assertEquals(nes3, nes3.asSequence().toSetF())

		assertEquals(setOf(9), nes1.asSequence().filter { it > 3 }.toSet())
		assertEquals(setOf(5), nes2.asSequence().filter { it > 3 }.toSet())
		assertEquals(setOf(4, 5), nes3.asSequence().filter { it > 3 }.toSet())
	}

	@Test
	fun coflatMap() {
		val f = { nes: NonEmptySet<Int> -> nes.sum() }
		assertEquals(NonEmptySet.just(9), nes1.coflatMap(f))
		assertEquals(nesOf(9, 4, 3), nes2.coflatMap(f))
		assertEquals(nesOf(11, 9, 5), nes3.coflatMap(f))
	}

	@Test
	fun equals_ignores_order() {
		assertEquals(nes1, nes1)
		assertEquals(nes3, nes3)
		assertEquals(nesOf(1, 5, 3), nesOf(3, 5, 1))
		assertEquals(nesOf(1, 5, 2), nesOf(1, 5, 2))
	}
}