package com.fsbarata.fp.types

import org.junit.Assert.assertEquals
import org.junit.Test

class NonEmptyListTest {
	val nel1 = NonEmptyList.just(9)
	val nel2 = NonEmptyList.of(5, 1, 3)

	@Test
	fun size() {
		assertEquals(1, nel1.size)
		assertEquals(3, nel2.size)
	}

	@Test
	fun first() {
		assertEquals(9, nel1.first())
		assertEquals(5, nel2.first())
	}

	@Test
	fun last() {
		assertEquals(9, nel1.last())
		assertEquals(3, nel2.last())
	}

	@Test
	fun get() {
		assertEquals(9, nel1[0])
		assertEquals(5, nel2[0])
		assertEquals(1, nel2[1])
		assertEquals(3, nel2[2])
	}

	@Test
	fun equals() {
		assertEquals(nel1.toList(), nel1)
		assertEquals(nel2.toList(), nel2)
	}

	@Test
	fun iterable() {
		val items = mutableListOf<Int>()
		nel1.forEach { items += it }
		assertEquals(nel1, items)
		items.clear()
		nel2.forEach { items += it }
		assertEquals(nel2, items)
	}

	@Test
	fun map() {
		assertEquals(NonEmptyList.just(45), nel1.map { it * 5 })
		assertEquals(NonEmptyList.of(25, 5, 15), nel2.map { it * 5 })
	}

	@Test
	fun flatMap() {
		assertEquals(NonEmptyList.of(90, 9), nel1.flatMap { NonEmptyList.of(10 * it, it) })
		assertEquals(NonEmptyList.of(50, 5, 10, 1, 30, 3), nel2.flatMap { NonEmptyList.of(10 * it, it) })
	}

	@Test
	fun fold() {
		assertEquals(45, nel1.fold(5, Int::times))
		assertEquals(30, nel2.fold(2, Int::times))
	}
}
