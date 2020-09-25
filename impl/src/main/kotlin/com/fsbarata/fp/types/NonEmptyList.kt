package com.fsbarata.fp.types

import com.fsbarata.fp.concepts.Context
import com.fsbarata.fp.concepts.Foldable
import com.fsbarata.fp.concepts.Functor
import com.fsbarata.fp.concepts.Monad
import java.io.Serializable

class NonEmptyList<out A> private constructor(
	val head: A,
	val tail: List<A>,
): AbstractList<A>(),
   List<A>,
   Monad<NonEmptyList<*>, A>,
   Foldable<A>,
   NonEmptyIterable<A>,
   Serializable {
	override val size: Int = 1 + tail.size

	override fun get(index: Int): A =
		if (index == 0) head
		else tail[index - 1]

	@Deprecated("Non empty list cannot be empty", replaceWith = ReplaceWith("false"))
	override fun isEmpty() = false

	fun first() = head

	@Deprecated("Non empty list always has a first", replaceWith = ReplaceWith("first()"))
	fun firstOrNull(): Nothing = throw UnsupportedOperationException()
	fun last() = if (tail.isEmpty()) head else tail.last()

	@Deprecated("Non empty list always has a last", replaceWith = ReplaceWith("last()"))
	fun lastOrNull(): Nothing = throw UnsupportedOperationException()

	override fun contains(element: @UnsafeVariance A) = head == element || tail.contains(element)
	override fun containsAll(elements: Collection<@UnsafeVariance A>) = elements.all(this::contains)

	override fun indexOf(element: @UnsafeVariance A) =
		if (head == element) 0
		else (tail.indexOf(element) + 1).takeIf { it != 0 } ?: -1

	override fun lastIndexOf(element: @UnsafeVariance A) =
		(tail.lastIndexOf(element) + 1).takeIf { it != 0 }
			?: if (head == element) 0 else -1

	override fun iterator(): NonEmptyIterator<A> =
		NonEmptyIterator(head, tail.iterator())

	override fun subList(fromIndex: Int, toIndex: Int): List<A> = when {
		fromIndex == 0 && toIndex == 0 -> emptyList()
		fromIndex == 0 -> NonEmptyList(head, tail.subList(0, toIndex - 1))
		else -> tail.subList(fromIndex - 1, toIndex - 1)
	}

	override fun <B> just(b: B): NonEmptyList<B> =
		Companion.just(b)

	override fun <B> map(f: (A) -> B): NonEmptyList<B> =
		NonEmptyList(f(head), tail.map(f))

	override fun <B> bind(f: (A) -> Functor<NonEmptyList<*>, B>): NonEmptyList<B> =
		flatMap { f(it).asNel }

	fun <B> flatMap(f: (A) -> NonEmptyList<B>): NonEmptyList<B> {
		val headList = f(head)
		return NonEmptyList(headList.head, headList.tail + tail.flatMap(f))
	}

	fun <B> flatMap(f: (A) -> List<B>): List<B> = f(head) + tail.flatMap(f)

	operator fun plus(other: @UnsafeVariance A) = NonEmptyList(head, tail + other)
	operator fun plus(other: Iterable<@UnsafeVariance A>) = NonEmptyList(head, tail + other)

	fun reversed() = tail.asReversed().nonEmpty()?.plus(head) ?: this

	fun <R: Comparable<R>> maxOf(selector: (A) -> R): R =
		tail.maxOfOrNull(selector)?.coerceAtLeast(selector(head)) ?: selector(head)

	fun <R: Comparable<R>> minOf(selector: (A) -> R): R =
		tail.minOfOrNull(selector)?.coerceAtMost(selector(head)) ?: selector(head)

	fun distinct() = NonEmptyList(head, (tail.toSet() - head).toList())
	fun <K> distinctBy(selector: (A) -> K): NonEmptyList<A> {
		val set = HashSet<K>()
		set.add(selector(head))
		return NonEmptyList(
			head,
			tail.filter { set.add(selector(it)) }
		)
	}

	fun asSequence() = NonEmptySequence { iterator() }

	companion object {
		fun <T> just(item: T) = of(item, emptyList())
		fun <T> of(head: T, vararg others: T) = of(head, others.toList())
		fun <T> of(head: T, others: List<T>) = NonEmptyList(head, others)
	}
}

val <A> Context<NonEmptyList<*>, A>.asNel get() = this as NonEmptyList<A>

fun <A> nelOf(head: A): NonEmptyList<A> = NonEmptyList.just(head)
fun <A> nelOf(head: A, vararg tail: A): NonEmptyList<A> = NonEmptyList.of(head, *tail)

fun <A> List<A>.startWithItem(item: A) = nelOf(item, this)

fun <A> List<A>.nonEmpty(): NonEmptyList<A>? = toNel()
fun <A> Iterable<A>.toNel(): NonEmptyList<A>? = when {
	this is NonEmptyList<A> -> this
	else -> iterator().nonEmpty()?.toNel()
}

internal fun <A> NonEmptyIterator<A>.toNel(): NonEmptyList<A> =
	NonEmptyList.of(head, tail.asSequence().toList())

fun <A> List<A>.concatNel(item: A) =
	nonEmpty()?.plus(item) ?: NonEmptyList.just(item)

fun <A> List<A>.concatNel(other: NonEmptyList<A>) = this + other
operator fun <A> List<A>.plus(other: NonEmptyList<A>) =
	nonEmpty()?.plus(other) ?: other

fun <T> NonEmptyList<NonEmptyList<T>>.flatten() = NonEmptyList.of(head.head, head.tail + tail.flatten())
fun <T: Comparable<T>> NonEmptyList<T>.max() = tail.maxOrNull()?.coerceAtLeast(head) ?: head
fun <T: Comparable<T>> NonEmptyList<T>.min() = tail.minOrNull()?.coerceAtMost(head) ?: head

fun <T, R> Iterable<T>.scanNel(initialValue: R, operation: (R, T) -> R) = NonEmptyList.of(
	initialValue,
	scan(initialValue, operation).drop(1)
)

fun <S, A: S> NonEmptyList<A>.runningReduceNel(operation: (S, A) -> S) =
	tail.scanNel(head, operation)
