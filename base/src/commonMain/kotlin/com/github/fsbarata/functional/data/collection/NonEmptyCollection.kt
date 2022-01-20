package com.github.fsbarata.functional.data.collection

import com.github.fsbarata.functional.data.Foldable
import com.github.fsbarata.functional.data.list.NonEmptyList
import com.github.fsbarata.functional.data.sequence.NonEmptySequence
import com.github.fsbarata.functional.data.set.NonEmptySet
import com.github.fsbarata.functional.kotlin.scanNel
import com.github.fsbarata.functional.utils.nonEmptyIterator
import kotlin.random.Random

interface NonEmptyCollection<out A>:
	Collection<A>,
	Foldable<A> {
	val head: A
	val tail: Collection<A>

	override val size: Int get() = 1 + tail.size

	@Deprecated("Non empty collection cannot be empty", replaceWith = ReplaceWith("false"))
	override fun isEmpty() = false

	fun first() = head

	@Deprecated("Non empty collection always has a first", replaceWith = ReplaceWith("first()"))
	fun firstOrNull(): Nothing = throw UnsupportedOperationException()
	fun last() = if (tail.isEmpty()) head else tail.last()

	@Deprecated("Non empty collection always has a last", replaceWith = ReplaceWith("last()"))
	fun lastOrNull(): Nothing = throw UnsupportedOperationException()

	@Deprecated("Non empty collection always has a random", replaceWith = ReplaceWith("random()"))
	fun randomOrNull(): Nothing = throw UnsupportedOperationException()

	@Deprecated("Non empty collection always has a random", replaceWith = ReplaceWith("random()"))
	fun randomOrNull(random: Random): Nothing = throw UnsupportedOperationException()

	override fun contains(element: @UnsafeVariance A) = head == element || tail.contains(element)
	override fun containsAll(elements: Collection<@UnsafeVariance A>) = elements.all(this::contains)

	override fun iterator() = nonEmptyIterator(head, tail.iterator())

	override fun <R> foldL(initialValue: R, accumulator: (R, A) -> R): R =
		tail.fold(accumulator(initialValue, head), accumulator)

	@Deprecated("Non empty collection always has a max", replaceWith = ReplaceWith("maxBy()"))
	fun <R: Comparable<R>> maxByOrNull(selector: (A) -> R): Nothing = throw UnsupportedOperationException()
	fun <R: Comparable<R>> maxBy(selector: (A) -> R): A = (this as Iterable<A>).maxByOrNull(selector)!!

	@Deprecated("Non empty collection always has a max", replaceWith = ReplaceWith("maxOf()"))
	fun <R: Comparable<R>> maxOfOrNull(selector: (A) -> R): Nothing = throw UnsupportedOperationException()
	fun <R: Comparable<R>> maxOf(selector: (A) -> R): R =
		tail.maxOfOrNull(selector)?.coerceAtLeast(selector(head)) ?: selector(head)

	@Deprecated("Non empty collection always has a min", replaceWith = ReplaceWith("minBy()"))
	fun <R: Comparable<R>> minByOrNull(selector: (A) -> R): Nothing = throw UnsupportedOperationException()
	fun <R: Comparable<R>> minBy(selector: (A) -> R): A = (this as Iterable<A>).minByOrNull(selector)!!

	@Deprecated("Non empty collection always has a min", replaceWith = ReplaceWith("minOf()"))
	fun <R: Comparable<R>> minOfOrNull(selector: (A) -> R): Nothing = throw UnsupportedOperationException()
	fun <R: Comparable<R>> minOf(selector: (A) -> R): R =
		tail.minOfOrNull(selector)?.coerceAtMost(selector(head)) ?: selector(head)

	infix fun union(other: NonEmptyCollection<@UnsafeVariance A>): NonEmptySet<A> =
		NonEmptySet.of(head, tail.union(other))

	fun toList() = NonEmptyList.of(head, tail.toList())
	fun toSet() = NonEmptySet.of(head, tail.toSet())

	fun asSequence(): NonEmptySequence<@UnsafeVariance A> = NonEmptySequence.of(head, tail)
}

fun <T> NonEmptyCollection<NonEmptyCollection<T>>.flattenToList() =
	NonEmptyList.of(head.head, head.tail + tail.flatten())

fun <T> NonEmptyCollection<NonEmptyCollection<T>>.flattenToSet() =
	NonEmptySet.of(head.head, head.tail.toSet() + tail.flatten())

inline fun <A, B> NonEmptyCollection<A>.flatMapIterable(f: (A) -> Iterable<B>): List<B> =
	f(head) + tail.flatMap(f)

fun <S, A: S> NonEmptyCollection<A>.runningReduceNel(operation: (S, A) -> S): NonEmptyList<S> =
	tail.scanNel(head, operation)

fun <T: Comparable<T>> NonEmptyCollection<T>.max() = tail.maxOrNull()?.coerceAtLeast(head) ?: head
fun <T: Comparable<T>> NonEmptyCollection<T>.min() = tail.minOrNull()?.coerceAtMost(head) ?: head
