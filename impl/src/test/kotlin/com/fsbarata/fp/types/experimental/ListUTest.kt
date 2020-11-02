package com.fsbarata.fp.types.experimental

import com.fsbarata.fp.concepts.Monad
import com.fsbarata.fp.concepts.test.FoldableTest
import com.fsbarata.fp.concepts.test.MonadTest
import com.fsbarata.fp.concepts.test.MonadZipTest
import com.fsbarata.fp.data.Foldable
import com.fsbarata.fp.types.experimental.ListU.NonEmpty
import com.fsbarata.utils.iterators.flatten
import com.fsbarata.utils.iterators.max
import com.fsbarata.utils.iterators.min
import com.fsbarata.utils.iterators.runningReduceNel
import org.junit.Assert
import org.junit.Test
import java.math.BigInteger
import kotlin.contracts.ExperimentalContracts

internal class NonEmptyTest: MonadTest<NonEmpty<*>>, MonadZipTest<NonEmpty<*>>, FoldableTest {
	override val monadScope = NonEmpty
	override fun Monad<NonEmpty<*>, Int>.equalTo(other: Monad<NonEmpty<*>, Int>) =
		asNel == other.asNel

	val nel1 = NonEmpty.just(9)
	val nel2 = NonEmpty.of(5, 1, 3)
	val nel3 = NonEmpty.of(2, NonEmpty.of(4, 2, 5))


	override fun createFoldable(item1: Int, item2: Int, item3: Int): Foldable<Int> =
		NonEmpty.of(item1, item2, item3)

	@Test
	fun size() {
		Assert.assertEquals(1, nel1.size)
		Assert.assertEquals(3, nel2.size)
		Assert.assertEquals(4, nel3.size)
	}

	@Test
	fun get() {
		Assert.assertEquals(9, nel1[0])
		Assert.assertEquals(5, nel2[0])
		Assert.assertEquals(1, nel2[1])
		Assert.assertEquals(3, nel2[2])
		Assert.assertEquals(2, nel3[0])
		Assert.assertEquals(4, nel3[1])
		Assert.assertEquals(2, nel3[2])
		Assert.assertEquals(5, nel3[3])
	}

	@Test
	fun indexOf() {
		Assert.assertEquals(0, nel1.indexOf(9))
		Assert.assertEquals(-1, nel1.indexOf(6))
		Assert.assertEquals(0, nel2.indexOf(5))
		Assert.assertEquals(1, nel2.indexOf(1))
		Assert.assertEquals(2, nel2.indexOf(3))
		Assert.assertEquals(-1, nel2.indexOf(6))
		Assert.assertEquals(0, nel3.indexOf(2))
		Assert.assertEquals(1, nel3.indexOf(4))
		Assert.assertEquals(3, nel3.indexOf(5))
		Assert.assertEquals(-1, nel3.indexOf(6))
	}

	@Test
	fun lastIndexOf() {
		Assert.assertEquals(0, nel1.lastIndexOf(9))
		Assert.assertEquals(-1, nel1.lastIndexOf(6))
		Assert.assertEquals(0, nel2.lastIndexOf(5))
		Assert.assertEquals(1, nel2.lastIndexOf(1))
		Assert.assertEquals(2, nel2.lastIndexOf(3))
		Assert.assertEquals(-1, nel2.lastIndexOf(6))
		Assert.assertEquals(1, nel3.lastIndexOf(4))
		Assert.assertEquals(2, nel3.lastIndexOf(2))
		Assert.assertEquals(3, nel3.lastIndexOf(5))
		Assert.assertEquals(-1, nel3.lastIndexOf(6))
	}

	@Test
	fun first() {
		Assert.assertEquals(9, nel1.first())
		Assert.assertEquals(5, nel2.first())
		Assert.assertEquals(2, nel3.first())
	}

	@Test
	fun last() {
		Assert.assertEquals(9, nel1.last())
		Assert.assertEquals(3, nel2.last())
		Assert.assertEquals(5, nel3.last())
	}

	@Test
	fun equals() {
		Assert.assertEquals(listOf(9), nel1)
		Assert.assertEquals(listOf(5, 1, 3), nel2)
		Assert.assertEquals(listOf(2, 4, 2, 5), nel3)
	}

	@Test
	fun iterable() {
		Assert.assertEquals(listOf(9), Iterable { nel1.iterator() }.toList())
		Assert.assertEquals(listOf(5, 1, 3), Iterable { nel2.iterator() }.toList())
		Assert.assertEquals(listOf(2, 4, 2, 5), Iterable { nel3.iterator() }.toList())
	}

	@Test
	fun sublist() {
		Assert.assertEquals(emptyList<Int>(), nel1.subList(0, 0))
		Assert.assertEquals(emptyList<Int>(), nel2.subList(0, 0))
		Assert.assertEquals(emptyList<Int>(), nel3.subList(0, 0))
		Assert.assertEquals(emptyList<Int>(), nel1.subList(1, 1))
		Assert.assertEquals(emptyList<Int>(), nel2.subList(1, 1))
		Assert.assertEquals(emptyList<Int>(), nel3.subList(1, 1))

		Assert.assertEquals(listOf(9), nel1.subList(0, 1))
		Assert.assertEquals(listOf(5), nel2.subList(0, 1))
		Assert.assertEquals(listOf(5, 1), nel2.subList(0, 2))
		Assert.assertEquals(listOf(2), nel3.subList(0, 1))
		Assert.assertEquals(listOf(2, 4), nel3.subList(0, 2))
		Assert.assertEquals(listOf(2, 4), nel3.subList(0, 2))

		Assert.assertEquals(nel2.toList(), nel2.subList(0, nel2.size))
		Assert.assertEquals(nel3.toList(), nel3.subList(0, nel3.size))

		Assert.assertEquals(listOf(1), nel2.subList(1, 2))
		Assert.assertEquals(listOf(1, 3), nel2.subList(1, 3))
		Assert.assertEquals(listOf(4), nel3.subList(1, 2))
		Assert.assertEquals(listOf(4, 2, 5), nel3.subList(1, 4))
	}

	@Test
	fun map() {
		Assert.assertEquals(NonEmpty.just(45), nel1.map { it * 5 })
		Assert.assertEquals(NonEmpty.of(25, 5, 15), nel2.map { it * 5 })
		Assert.assertEquals(NonEmpty.of(10, 20, 10, 25), nel3.map { it * 5 })
	}

	@Test
	fun flatMap() {
		Assert.assertEquals(NonEmpty.of(90, 9), nel1.flatMap { NonEmpty.of(10 * it, it) })
		Assert.assertEquals(NonEmpty.of(50, 5, 10, 1, 30, 3), nel2.flatMap { NonEmpty.of(10 * it, it) })
		Assert.assertEquals(NonEmpty.of(20, 2, 40, 4, 20, 2, 50, 5), nel3.flatMap { NonEmpty.of(10 * it, it) })
	}

	@Test
	fun fold() {
		Assert.assertEquals(45L, nel1.foldL(5L, Long::times))
		Assert.assertEquals(30L, nel2.foldL(2L, Long::times))
		Assert.assertEquals(240L, nel3.foldL(3L, Long::times))
	}

	@Test
	fun plus() {
		Assert.assertEquals(NonEmpty.of(9, 3), nel1 + 3)
		Assert.assertEquals(NonEmpty.of(5, 1, 3, 3), nel2 + 3)
		Assert.assertEquals(NonEmpty.of(2, 4, 2, 5, 3), nel3 + 3)

		Assert.assertEquals(nel1, nel1 + emptyList())
		Assert.assertEquals(nel2, nel2 + emptyList())
		Assert.assertEquals(nel3, nel3 + emptyList())

		Assert.assertEquals(NonEmpty.of(9, 5, 5), nel1 + listOf(5, 5))
		Assert.assertEquals(NonEmpty.of(5, 1, 3, 1, 2, 6), nel2 + listOf(1, 2, 6))
		Assert.assertEquals(NonEmpty.of(2, 4, 2, 5, 3), nel3 + listOf(3))
	}

	@Test
	fun reversed() {
		Assert.assertEquals(nel1, nel1.reversed())
		Assert.assertEquals(NonEmpty.of(3, 1, 5), nel2.reversed())
		Assert.assertEquals(NonEmpty.of(5, 2, 4, 2), nel3.reversed())
	}

	@Test
	fun max() {
		Assert.assertEquals(9, nel1.max())
		Assert.assertEquals(5, nel2.max())
		Assert.assertEquals(5, nel3.max())
	}

	@Test
	fun min() {
		Assert.assertEquals(9, nel1.min())
		Assert.assertEquals(1, nel2.min())
		Assert.assertEquals(2, nel3.min())
	}

	@Test
	fun maxOf() {
		Assert.assertEquals(BigInteger.valueOf(9), nel1.maxOf { BigInteger.valueOf(it.toLong()) })
		Assert.assertEquals(BigInteger.valueOf(2), nel2.maxOf { BigInteger.valueOf(it % 3L) })
		Assert.assertEquals(BigInteger.valueOf(4), nel3.maxOf { BigInteger.valueOf(it % 5L) })
	}

	@Test
	fun minOf() {
		Assert.assertEquals(BigInteger.valueOf(9), nel1.minOf { BigInteger.valueOf(it.toLong()) })
		Assert.assertEquals(BigInteger.valueOf(0), nel2.minOf { BigInteger.valueOf(it % 3L) })
		Assert.assertEquals(BigInteger.valueOf(0), nel3.minOf { BigInteger.valueOf(it % 5L) })
	}

	@Test
	fun distinct() {
		Assert.assertEquals(nel1, nel1.distinct())
		Assert.assertEquals(nel2, nel2.distinct())
		Assert.assertEquals(NonEmpty.of(2, 4, 5), nel3.distinct())
		Assert.assertEquals(NonEmpty.just(2), NonEmpty.of(2, 2, 2).distinct())
		Assert.assertEquals(NonEmpty.of(2, 4, 5), NonEmpty.of(2, 4, 5, 4).distinct())
	}

	@Test
	fun distinctBy() {
		Assert.assertEquals(nel1, nel1.distinctBy { it })
		Assert.assertEquals(NonEmpty.of(5, 3), nel2.distinctBy { it % 4 })
		Assert.assertEquals(NonEmpty.of(2, 5), nel3.distinctBy { it % 2 })
	}

	@Test
	fun flatten() {
		Assert.assertEquals(
			NonEmpty.of(3, 5, 1, 3, 9),
			NonEmpty.of(
				NonEmpty.of(3, 5),
				listOf(
					NonEmpty.of(1, 3),
					NonEmpty.just(9)
				)
			).flatten()
		)
	}

	@Test
	fun asSequence() {
		Assert.assertEquals(nel1, nel1.asSequence().toList())
		Assert.assertEquals(nel2, nel2.asSequence().toList())
		Assert.assertEquals(nel3, nel3.asSequence().toList())

		Assert.assertEquals(listOf(9), nel1.asSequence().filter { it > 3 }.toList())
		Assert.assertEquals(listOf(5), nel2.asSequence().filter { it > 3 }.toList())
		Assert.assertEquals(listOf(4, 5), nel3.asSequence().filter { it > 3 }.toList())
	}

	@Test
	fun runningReduceNel() {
		Assert.assertEquals(NonEmpty.just(9), nel1.runningReduceNel { acc, i -> acc + i - 2 })
		Assert.assertEquals(NonEmpty.of(5, 4, 5), nel2.runningReduceNel { acc, i -> acc + i - 2 })
		Assert.assertEquals(NonEmpty.of(2, 4, 4, 7), nel3.runningReduceNel { acc, i -> acc + i - 2 })
	}

	@ExperimentalContracts
	@Test
	fun compilesNonEmpty() {
		val list = listOf(3).u()
		if (list.isNotEmptyContract()) {
			list.extensionNe()
		} else {
			list.extensionEmpty()
		}
	}

	private fun NonEmpty<*>.extensionNe() {}
	private fun ListU.Empty.extensionEmpty() {}
}
