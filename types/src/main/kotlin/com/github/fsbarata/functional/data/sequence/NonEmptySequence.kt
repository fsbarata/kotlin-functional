package com.github.fsbarata.functional.data.sequence

import com.github.fsbarata.functional.control.Context
import com.github.fsbarata.functional.control.Monad
import com.github.fsbarata.functional.control.MonadZip
import com.github.fsbarata.functional.data.list.NonEmptyList
import com.github.fsbarata.functional.iterators.EmptyIterator
import com.github.fsbarata.functional.iterators.NonEmptyIterator
import com.github.fsbarata.functional.iterators.toNel

/**
 * A NonEmpty sequence.
 *
 * By definition, this object is guaranteed to have at least one item. The head item is still lazily acquired.
 */
interface NonEmptySequence<out A>:
	Sequence<A>,
	Monad<NonEmptySequence<*>, A>,
	MonadZip<NonEmptySequence<*>, A> {
	override val scope get() = Companion

	override fun iterator(): NonEmptyIterator<A>

	fun <R> foldL(initialValue: R, accumulator: (R, A) -> R): R =
		fold(initialValue, accumulator)

	@Deprecated("Non empty sequence always has a first", replaceWith = ReplaceWith("first()"))
	fun firstOrNull(): Nothing = throw UnsupportedOperationException()
	fun first() = iterator().head

	@Deprecated("Non empty sequence always has a last", replaceWith = ReplaceWith("last()"))
	fun lastOrNull(): Nothing = throw UnsupportedOperationException()
	fun last() = iterator().let { it.tail.asSequence().lastOrNull() ?: it.head }

	fun toList(): NonEmptyList<A> = iterator().toNel()

	operator fun plus(element: @UnsafeVariance A): NonEmptySequence<A> = plus(sequenceOf(element))

	operator fun plus(elements: Sequence<@UnsafeVariance A>): NonEmptySequence<A> = NonEmptySequence {
		val iterator = iterator()
		NonEmptyIterator(iterator.head, (iterator.tail.asSequence() + elements).iterator())
	}

	override fun <B, R> zipWith(other: MonadZip<NonEmptySequence<*>, B>, f: (A, B) -> R): NonEmptySequence<R> {
		val otherNes = other.asNes
		return NonEmptySequence {
			val iterator1 = iterator()
			val iterator2 = otherNes.iterator()
			NonEmptyIterator(
				f(iterator1.head, iterator2.head),
				iterator1.tail.asSequence().zip(iterator2.asSequence(), f).iterator()
			)
		}
	}

	override fun <B> map(f: (A) -> B) = NonEmptySequence {
		val iterator = iterator()
		NonEmptyIterator(
			f(iterator.head),
			iterator.tail.asSequence().map(f).iterator()
		)
	}

	override fun <B> bind(f: (A) -> Context<NonEmptySequence<*>, B>) =
		flatMap { f(it).asNes }

	fun <B> flatMap(f: (A) -> NonEmptySequence<B>) = NonEmptySequence {
		val iterator = iterator()
		val headIterator = f(iterator.head).asNes.iterator()
		NonEmptyIterator(
			headIterator.head,
			(headIterator.tail.asSequence() + iterator.tail.asSequence().flatMap(f)).iterator()
		)
	}

	companion object: Monad.Scope<NonEmptySequence<*>> {
		override fun <A> just(a: A) = NonEmptySequence { NonEmptyIterator(a, EmptyIterator) }
	}
}

val <A> Context<NonEmptySequence<*>, A>.asNes get() = this as NonEmptySequence<A>

inline fun <A> NonEmptySequence(crossinline iterator: () -> NonEmptyIterator<A>): NonEmptySequence<A> =
	object: NonEmptySequence<A> {
		override fun iterator() = iterator()
	}

fun <A: Any> nonEmptySequence(head: A, nextFunction: (A) -> A?) = NonEmptySequence {
	NonEmptyIterator(
		head,
		generateSequence(nextFunction(head), nextFunction).iterator()
	)
}

fun <A> Sequence<A>.startWithItem(item: A) =
	NonEmptySequence { NonEmptyIterator(item, iterator()) }

fun <A> nonEmptySequenceOf(head: A, vararg tail: A) =
	NonEmptySequence { NonEmptyIterator(head, tail.iterator()) }

fun <A> Sequence<A>.nonEmpty(ifEmpty: NonEmptySequence<A>) = nonEmpty(ifEmpty::iterator)

fun <A> Sequence<A>.nonEmpty(ifEmpty: () -> NonEmptyIterator<A>) =
	NonEmptySequence {
		val iterator = iterator()
		if (iterator.hasNext()) NonEmptyIterator(iterator.next(), iterator)
		else ifEmpty()
	}


