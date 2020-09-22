package com.fsbarata.fp.types

import org.junit.Assert.assertEquals
import org.junit.Test
import java.math.BigInteger

class NonEmptyListTest {
	val nel1 = NonEmptyList.just(9)
	val nel2 = NonEmptyList.of(5, 1, 3)
	val nel3 = NonEmptyList.of(2, NonEmptyList.of(4, 2, 5))

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
		assertEquals(listOf(9), nel1)
		assertEquals(listOf(5, 1, 3), nel2)
		assertEquals(listOf(2, 4, 2, 5), nel3)
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

		assertEquals(nel2, nel2.subList(0, nel2.size))
		assertEquals(nel3, nel3.subList(0, nel3.size))

		assertEquals(listOf(1), nel2.subList(1, 2))
		assertEquals(listOf(1, 3), nel2.subList(1, 3))
		assertEquals(listOf(4), nel3.subList(1, 2))
		assertEquals(listOf(4, 2, 5), nel3.subList(1, 4))
	}

	@Test
	fun map() {
		assertEquals(NonEmptyList.just(45), nel1.map { it * 5 })
		assertEquals(NonEmptyList.of(25, 5, 15), nel2.map { it * 5 })
		assertEquals(NonEmptyList.of(10, 20, 10, 25), nel3.map { it * 5 })
	}

	@Test
	fun flatMap() {
		assertEquals(NonEmptyList.of(90, 9), nel1.flatMap { NonEmptyList.of(10 * it, it) })
		assertEquals(NonEmptyList.of(50, 5, 10, 1, 30, 3), nel2.flatMap { NonEmptyList.of(10 * it, it) })
		assertEquals(NonEmptyList.of(20, 2, 40, 4, 20, 2, 50, 5), nel3.flatMap { NonEmptyList.of(10 * it, it) })
	}

	@Test
	fun fold() {
		assertEquals(45L, nel1.fold(5L, Long::times))
		assertEquals(30L, nel2.fold(2L, Long::times))
		assertEquals(240L, nel3.fold(3L, Long::times))
	}

	@Test
	fun plus() {
		assertEquals(NonEmptyList.of(9, 3), nel1 + 3)
		assertEquals(NonEmptyList.of(5, 1, 3, 3), nel2 + 3)
		assertEquals(NonEmptyList.of(2, 4, 2, 5, 3), nel3 + 3)

		assertEquals(nel1, nel1 + emptyList())
		assertEquals(nel2, nel2 + emptyList())
		assertEquals(nel3, nel3 + emptyList())

		assertEquals(NonEmptyList.of(9, 5, 5), nel1 + listOf(5, 5))
		assertEquals(NonEmptyList.of(5, 1, 3, 1, 2, 6), nel2 + listOf(1, 2, 6))
		assertEquals(NonEmptyList.of(2, 4, 2, 5, 3), nel3 + listOf(3))
	}

	@Test
	fun concatNel() {
		assertEquals(NonEmptyList.of(9, 3), listOf(9).concatNel(3))
		assertEquals(NonEmptyList.of(5, 1, 3, 3), listOf(5, 1, 3).concatNel(3))
		assertEquals(NonEmptyList.of(5, 1, 3, 3), listOf(5, 1).concatNel(NonEmptyList.of(3, 3)))
		assertEquals(NonEmptyList.just(5), emptyList<Int>().concatNel(5))
		assertEquals(NonEmptyList.just(5), emptyList<Int>().concatNel(NonEmptyList.just(5)))
	}

	@Test
	fun reversed() {
		assertEquals(nel1, nel1.reversed())
		assertEquals(NonEmptyList.of(3, 1, 5), nel2.reversed())
		assertEquals(NonEmptyList.of(5, 2, 4, 2), nel3.reversed())
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
		assertEquals(NonEmptyList.of(2, 4, 5), nel3.distinct())
		assertEquals(NonEmptyList.just(2), NonEmptyList.of(2, 2, 2).distinct())
		assertEquals(NonEmptyList.of(2, 4, 5), NonEmptyList.of(2, 4, 5, 4).distinct())
	}

	@Test
	fun distinctBy() {
		assertEquals(nel1, nel1.distinctBy { it })
		assertEquals(NonEmptyList.of(5, 3), nel2.distinctBy { it % 4 })
		assertEquals(NonEmptyList.of(2, 5), nel3.distinctBy { it % 2 })
	}

	@Test
	fun flatten() {
		assertEquals(
				NonEmptyList.of(3, 5, 1, 3, 9),
				NonEmptyList.of(
						NonEmptyList.of(3, 5),
						listOf(
								NonEmptyList.of(1, 3),
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
}
