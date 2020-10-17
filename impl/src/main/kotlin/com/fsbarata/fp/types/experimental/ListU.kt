package com.fsbarata.fp.types.experimental

import com.fsbarata.fp.concepts.*
import com.fsbarata.fp.types.*
import java.io.Serializable

internal sealed class ListU<out A>
	: Monad<ListU<*>, A>,
	  Foldable<A>,
	  List<A>,
	  Serializable {
	override val scope get() = Companion

	object Empty: ListU<Nothing>() {
		@Deprecated("Empty list is always empty", replaceWith = ReplaceWith("true"))
		override fun isEmpty() = true

		@Deprecated("Empty list is always empty", replaceWith = ReplaceWith("0"))
		override val size: Int = 0

		@Deprecated("Empty list is always empty", replaceWith = ReplaceWith("null"))
		fun firstOrNull() = null

		@Deprecated("Empty list is always empty", replaceWith = ReplaceWith("null"))
		fun lastOrNull() = null

		@Deprecated("Empty list does not contain anything", replaceWith = ReplaceWith("false"))
		override fun contains(element: Nothing) = false

		@Deprecated("Empty list does not contain anything", replaceWith = ReplaceWith("false"))
		override fun containsAll(elements: Collection<Nothing>) = false

		@Deprecated("Empty list does not contain anything", replaceWith = ReplaceWith("Nothing"))
		override fun get(index: Int): Nothing = throw ArrayIndexOutOfBoundsException(index)
		override fun <B> bind(f: (Nothing) -> Functor<ListU<*>, B>) = this

		@Deprecated("Empty list is always empty", replaceWith = ReplaceWith("-1"))
		override fun indexOf(element: Nothing): Int = -1

		@Deprecated("Empty list is always empty", replaceWith = ReplaceWith("-1"))
		override fun lastIndexOf(element: Nothing): Int = -1

		override fun iterator() = object: Iterator<Nothing> {
			override fun hasNext() = false
			override fun next() = throw NoSuchElementException()
		}

		override fun listIterator() = object: ListIterator<Nothing> {
			override fun hasNext() = false
			override fun hasPrevious() = false
			override fun next() = throw NoSuchElementException()
			override fun nextIndex() = 0
			override fun previous() = throw NoSuchElementException()
			override fun previousIndex() = -1
		}

		override fun listIterator(index: Int) = listIterator()

		override fun subList(fromIndex: Int, toIndex: Int) =
			if (fromIndex == 0 && toIndex == 0) this
			else throw IndexOutOfBoundsException("Empty list cannot be split")
	}

	class NonEmpty<out A> private constructor(
		override val head: A,
		override val tail: List<A>,
	): ListU<A>(),
	   NonEmptyIterable<A> {
		@Deprecated("Non empty list is never empty", replaceWith = ReplaceWith("false"))
		override fun isEmpty(): Boolean = false

		override val size: Int = 1 + tail.size

		override fun get(index: Int): A =
			if (index == 0) head
			else tail[index - 1]

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
			fromIndex == 0 -> NonEmpty(head, tail.subList(0, toIndex - 1))
			else -> tail.subList(fromIndex - 1, toIndex - 1)
		}

		override inline fun <B> map(f: (A) -> B): NonEmpty<B> = of(f(head), tail.map(f))

		override fun <R> fold(initialValue: R, accumulator: (R, A) -> R): R =
			tail.fold(accumulator(initialValue, head), accumulator)

		inline fun <B> flatMap(f: (A) -> NonEmptyList<B>): NonEmptyList<B> = map(f).flatten()

		inline fun <B> flatMapIterable(f: (A) -> List<B>): List<B> = f(head) + tail.flatMap(f)

		operator fun plus(other: @UnsafeVariance A) = NonEmpty(head, tail + other)
		operator fun plus(other: Iterable<@UnsafeVariance A>) = NonEmpty(head, tail + other)

		fun reversed() = tail.asReversed().nonEmpty()?.plus(head) ?: this

		fun <R: Comparable<R>> maxOf(selector: (A) -> R): R =
			tail.maxOfOrNull(selector)?.coerceAtLeast(selector(head)) ?: selector(head)

		fun <R: Comparable<R>> minOf(selector: (A) -> R): R =
			tail.minOfOrNull(selector)?.coerceAtMost(selector(head)) ?: selector(head)

		fun distinct() = NonEmpty(head, (tail.toSet() - head).toList())
		fun <K> distinctBy(selector: (A) -> K): NonEmpty<A> {
			val set = HashSet<K>()
			set.add(selector(head))
			return of(
				head,
				tail.filter { set.add(selector(it)) }
			)
		}

		fun asSequence() = NonEmptySequence { iterator() }

		override fun listIterator(): ListIterator<Nothing> {
			TODO("Not yet implemented")
		}

		override fun listIterator(index: Int): ListIterator<Nothing> {
			TODO("Not yet implemented")
		}

		companion object {
			fun <T> just(item: T) = of(item, emptyList())
			fun <T> of(head: T, vararg others: T) = of(head, others.toList())
			fun <T> of(head: T, others: List<T>) = NonEmpty(head, others)
		}
	}

	override fun <B> bind(f: (A) -> Functor<ListU<*>, B>) =
		flatMap { f(it).asList }

	inline fun <B> flatMap(f: (A) -> List<B>) =
		(this as List<A>).flatMap(f).u()

	override fun <R> fold(initialValue: R, accumulator: (R, A) -> R): R =
		when (this) {
			is Empty -> initialValue
			is NonEmpty -> (this as NonEmpty).fold(initialValue, accumulator)
		}

	companion object: Monad.Scope<ListU<*>> {
		fun <A> empty() = Empty
		override fun <A> just(a: A) = NonEmpty.just(a)
	}
}

internal fun <A> List<A>.u() = toNel() ?: ListU.Empty

internal val <A> Context<ListU<*>, A>.asList: ListU<A>
	get() = this as ListU<A>


internal fun <A> Iterable<A>.toNel(): ListU.NonEmpty<A>? {
	return when (this) {
		is ListU.NonEmpty<A> -> this
		else -> iterator().nonEmpty()?.toNel()
	}
}

internal fun <A> NonEmptyIterator<A>.toNel(): ListU.NonEmpty<A> =
	ListU.NonEmpty.of(head, tail.asSequence().toList())
