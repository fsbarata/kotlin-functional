package com.github.fsbarata.functional.data.list

import com.github.fsbarata.functional.control.ComonadLaws
import com.github.fsbarata.functional.control.MonadZipLaws
import com.github.fsbarata.functional.data.TraversableLaws
import com.github.fsbarata.functional.data.collection.max
import com.github.fsbarata.functional.data.collection.min
import com.github.fsbarata.functional.data.collection.runningReduceNel
import com.github.fsbarata.functional.data.maybe.Optional
import com.github.fsbarata.functional.data.set.nesOf
import com.github.fsbarata.functional.data.validation.Validation
import com.github.fsbarata.functional.data.validation.ValidationApplicativeScope
import com.github.fsbarata.functional.data.validation.asValidation
import org.junit.Assert.assertEquals
import org.junit.Test
import java.math.BigInteger

class NonEmptyListTest:
	MonadZipLaws<NonEmptyContext>,
	TraversableLaws<NonEmptyContext>,
	ComonadLaws<NonEmptyContext> {
	override val monadScope = NonEmptyList
	override val traversableScope = NonEmptyList

	override fun <A> createTraversable(vararg items: A) =
		items.toList().toNel() ?: throw NoSuchElementException()

	override val possibilities = 10
	override fun factory(possibility: Int) = createNel(possibility)

	private val nel1 = NonEmptyList.just(9)
	private val nel2 = nelOf(5, 1, 3)
	private val nel3 = NonEmptyList.of(2, nelOf(4, 2, 5))

	@Test
	fun size() {
		assertEquals(1, nel1.size)
		assertEquals(3, nel2.size)
		assertEquals(4, nel3.size)
	}

	@Test
	fun get() {
		assertEquals(9, nel1[0])
		assertEquals(5, nel2[0])
		assertEquals(1, nel2[1])
		assertEquals(3, nel2[2])
		assertEquals(2, nel3[0])
		assertEquals(4, nel3[1])
		assertEquals(2, nel3[2])
		assertEquals(5, nel3[3])
	}

	@Test
	fun indexOf() {
		assertEquals(0, nel1.indexOf(9))
		assertEquals(-1, nel1.indexOf(6))
		assertEquals(0, nel2.indexOf(5))
		assertEquals(1, nel2.indexOf(1))
		assertEquals(2, nel2.indexOf(3))
		assertEquals(-1, nel2.indexOf(6))
		assertEquals(0, nel3.indexOf(2))
		assertEquals(1, nel3.indexOf(4))
		assertEquals(3, nel3.indexOf(5))
		assertEquals(-1, nel3.indexOf(6))
	}

	@Test
	fun lastIndexOf() {
		assertEquals(0, nel1.lastIndexOf(9))
		assertEquals(-1, nel1.lastIndexOf(6))
		assertEquals(0, nel2.lastIndexOf(5))
		assertEquals(1, nel2.lastIndexOf(1))
		assertEquals(2, nel2.lastIndexOf(3))
		assertEquals(-1, nel2.lastIndexOf(6))
		assertEquals(1, nel3.lastIndexOf(4))
		assertEquals(2, nel3.lastIndexOf(2))
		assertEquals(3, nel3.lastIndexOf(5))
		assertEquals(-1, nel3.lastIndexOf(6))
	}

	@Test
	fun first() {
		assertEquals(9, nel1.first())
		assertEquals(5, nel2.first())
		assertEquals(2, nel3.first())
	}

	@Test
	fun last() {
		assertEquals(9, nel1.last())
		assertEquals(3, nel2.last())
		assertEquals(5, nel3.last())
	}

	@Test
	fun equals() {
		assertEquals(nelOf(9), nel1)
		assertEquals(nelOf(5, 1, 3), nel2)
		assertEquals(nelOf(2, 4, 2, 5), nel3)
	}

	@Test
	fun iterable() {
		assertEquals(listOf(9), Iterable { nel1.iterator() }.toList())
		assertEquals(listOf(5, 1, 3), Iterable { nel2.iterator() }.toList())
		assertEquals(listOf(2, 4, 2, 5), Iterable { nel3.iterator() }.toList())
	}

	@Test
	fun sublist() {
		assertEquals(emptyList<Int>(), nel1.subList(0, 0))
		assertEquals(emptyList<Int>(), nel2.subList(0, 0))
		assertEquals(emptyList<Int>(), nel3.subList(0, 0))
		assertEquals(emptyList<Int>(), nel1.subList(1, 1))
		assertEquals(emptyList<Int>(), nel2.subList(1, 1))
		assertEquals(emptyList<Int>(), nel3.subList(1, 1))

		assertEquals(listOf(9), nel1.subList(0, 1))
		assertEquals(listOf(5), nel2.subList(0, 1))
		assertEquals(listOf(5, 1), nel2.subList(0, 2))
		assertEquals(listOf(2), nel3.subList(0, 1))
		assertEquals(listOf(2, 4), nel3.subList(0, 2))
		assertEquals(listOf(2, 4), nel3.subList(0, 2))

		assertEquals(nel2.toList(), nel2.subList(0, nel2.size))
		assertEquals(nel3.toList(), nel3.subList(0, nel3.size))

		assertEquals(listOf(1), nel2.subList(1, 2))
		assertEquals(listOf(1, 3), nel2.subList(1, 3))
		assertEquals(listOf(4), nel3.subList(1, 2))
		assertEquals(listOf(4, 2, 5), nel3.subList(1, 4))
	}

	@Test
	fun map() {
		assertEquals(NonEmptyList.just(45), nel1.map { it * 5 })
		assertEquals(nelOf(25, 5, 15), nel2.map { it * 5 })
		assertEquals(nelOf(10, 20, 10, 25), nel3.map { it * 5 })
	}

	@Test
	fun mapIndexed() {
		assertEquals(NonEmptyList.just(18), nel1.mapIndexed { index, item -> item * (2 + index) })
		assertEquals(nelOf(10, 3, 12), nel2.mapIndexed { index, item -> item * (2 + index) })
		assertEquals(nelOf(4, 4, 0, -5), nel3.mapIndexed { index, item -> item * (2 - index) })
	}

	@Test
	fun flatMap() {
		assertEquals(nelOf(90, 9), nel1.flatMap { nelOf(10 * it, it) })
		assertEquals(nelOf(50, 5, 10, 1, 30, 3), nel2.flatMap { nelOf(10 * it, it) })
		assertEquals(nelOf(20, 2, 40, 4, 20, 2, 50, 5), nel3.flatMap { nelOf(10 * it, it) })
	}

	@Test
	fun flatMapIndexed() {
		assertEquals(nelOf(90, 9), nel1.flatMapIndexed { index, item -> nelOf(10 * item, item + index) })
		assertEquals(nelOf(50, 5, 10, 2, 30, 5), nel2.flatMapIndexed { index, item -> nelOf(10 * item, item + index) })
		assertEquals(
			nelOf(20, 2, 40, 5, 20, 4, 50, 8),
			nel3.flatMapIndexed { index, item -> nelOf(10 * item, item + index) }
		)
	}

	@Test
	fun fold() {
		assertEquals(45L, nel1.foldL(5L, Long::times))
		assertEquals(30L, nel2.foldL(2L, Long::times))
		assertEquals(240L, nel3.foldL(3L, Long::times))
	}

	@Test
	fun plus() {
		assertEquals(nelOf(9, 3), nel1 + 3)
		assertEquals(nelOf(5, 1, 3, 3), nel2 + 3)
		assertEquals(nelOf(2, 4, 2, 5, 3), nel3 + 3)

		assertEquals(nel1, nel1 + emptyList())
		assertEquals(nel2, nel2 + emptyList())
		assertEquals(nel3, nel3 + emptyList())

		assertEquals(nelOf(9, 5, 5), nel1 + listOf(5, 5))
		assertEquals(nelOf(5, 1, 3, 1, 2, 6), nel2 + listOf(1, 2, 6))
		assertEquals(nelOf(2, 4, 2, 5, 3), nel3 + listOf(3))
	}

	@Test
	fun reversed() {
		assertEquals(nel1, nel1.reversed())
		assertEquals(nelOf(3, 1, 5), nel2.reversed())
		assertEquals(nelOf(5, 2, 4, 2), nel3.reversed())
	}

	@Test
	fun max() {
		assertEquals(9, nel1.max())
		assertEquals(5, nel2.max())
		assertEquals(5, nel3.max())
	}

	@Test
	fun min() {
		assertEquals(9, nel1.min())
		assertEquals(1, nel2.min())
		assertEquals(2, nel3.min())
	}

	@Test
	fun maxOf() {
		assertEquals(BigInteger.valueOf(9), nel1.maxOf { BigInteger.valueOf(it.toLong()) })
		assertEquals(BigInteger.valueOf(2), nel2.maxOf { BigInteger.valueOf(it % 3L) })
		assertEquals(BigInteger.valueOf(4), nel3.maxOf { BigInteger.valueOf(it % 5L) })
	}

	@Test
	fun minOf() {
		assertEquals(BigInteger.valueOf(9), nel1.minOf { BigInteger.valueOf(it.toLong()) })
		assertEquals(BigInteger.valueOf(0), nel2.minOf { BigInteger.valueOf(it % 3L) })
		assertEquals(BigInteger.valueOf(0), nel3.minOf { BigInteger.valueOf(it % 5L) })
	}

	@Test
	fun distinct() {
		assertEquals(nel1, nel1.distinct())
		assertEquals(nel2, nel2.distinct())
		assertEquals(nelOf(2, 4, 5), nel3.distinct())
		assertEquals(NonEmptyList.just(2), nelOf(2, 2, 2).distinct())
		assertEquals(nelOf(2, 4, 5), nelOf(2, 4, 5, 4).distinct())
	}

	@Test
	fun distinctBy() {
		assertEquals(nel1, nel1.distinctBy { it })
		assertEquals(nelOf(5, 3), nel2.distinctBy { it % 4 })
		assertEquals(nelOf(2, 5), nel3.distinctBy { it % 2 })
	}

	@Test
	fun union() {
		assertEquals(nesOf(9, 5, 1, 3), nel1.union(nel2))
		assertEquals(nesOf(2, 4, 5, 1, 3), nel3.union(nel2))
		assertEquals(nesOf(3, 5, 2, 4), nelOf(3, 5, 2).union(nesOf(4, 3, 2)))
	}

	@Test
	fun flatten() {
		assertEquals(
			nelOf(3, 5, 1, 3, 9),
			NonEmptyList.of(
				nelOf(3, 5),
				listOf(
					nelOf(1, 3),
					NonEmptyList.just(9)
				)
			).flatten()
		)
	}

	@Test
	fun asSequence() {
		assertEquals(nel1, nel1.asSequence().toList())
		assertEquals(nel2, nel2.asSequence().toList())
		assertEquals(nel3, nel3.asSequence().toList())

		assertEquals(listOf(9), nel1.asSequence().filter { it > 3 }.toList())
		assertEquals(listOf(5), nel2.asSequence().filter { it > 3 }.toList())
		assertEquals(listOf(4, 5), nel3.asSequence().filter { it > 3 }.toList())
	}

	@Test
	fun coflatMap() {
		val f = { nel: NonEmptyList<Int> -> nel.sum() }
		assertEquals(NonEmptyList.just(9), nel1.coflatMap(f))
		assertEquals(nelOf(9, 4, 3), nel2.coflatMap(f))
		assertEquals(nelOf(13, 11, 7, 5), nel3.coflatMap(f))
	}

	@Test
	fun runningReduceNel() {
		assertEquals(NonEmptyList.just(9), nel1.runningReduceNel { acc, i -> acc + i - 2 })
		assertEquals(nelOf(5, 4, 5), nel2.runningReduceNel { acc, i -> acc + i - 2 })
		assertEquals(nelOf(2, 4, 4, 7), nel3.runningReduceNel { acc, i -> acc + i - 2 })
	}

	@Test
	fun traverse() {
		assertEquals(
			Optional.just(listOf("5", "7", "3")),
			nelOf(3, 5, 1)
				.traverse { a -> Optional.just("${a + 2}") }
		)

		assertEquals(
			Optional.empty<String>(),
			nelOf(3, 5, 1)
				.traverse { a ->
					if (a >= 5) Optional.empty()
					else Optional.just("${a + 2}")
				}
		)

		assertEquals(
			Validation.success<NonEmptyList<String>, Int>(2),
			nelOf(5, 2, 1)
				.traverse(ValidationApplicativeScope()) { a ->
					when {
						a < 1 -> Validation.Failure(NonEmptyList.just("a"))
						a < 3 -> Validation.success(1)
						else -> Validation.success(0)
					}
				}
				.asValidation
				.map { it.sum() }
		)
	}

	@Test
	fun sequenceA() {
		assertEquals(
			Optional.just(nelOf(3, 5, 1)),
			nelOf(
				Optional.just(3),
				Optional.just(5),
				Optional.just(1)
			).sequenceA()
		)

		assertEquals(
			Optional.empty<Int>(),
			nelOf(
				Optional.just(3),
				Optional.empty(),
				Optional.just(1)
			).sequenceA()
		)

		assertEquals(
			Validation.success<NonEmptyList<String>, Int>(2),
			nelOf(
				Validation.success<NonEmptyList<String>, Int>(1),
				Validation.success(0),
				Validation.success(1),
			)
				.sequenceA(ValidationApplicativeScope())
				.asValidation
				.map { it.sum() }
		)
	}
}
