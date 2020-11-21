package com.github.fsbarata.functional.data.sequence

import com.github.fsbarata.functional.Context
import com.github.fsbarata.functional.control.Applicative
import com.github.fsbarata.functional.control.Monad
import com.github.fsbarata.functional.control.MonadZip
import com.github.fsbarata.functional.data.Foldable
import com.github.fsbarata.functional.data.Semigroup
import com.github.fsbarata.functional.data.Traversable
import com.github.fsbarata.functional.data.list.NonEmptyList
import com.github.fsbarata.functional.utils.NonEmptyIterator
import com.github.fsbarata.functional.utils.toNel

internal typealias NonEmptySequenceContext = NonEmptySequence<*>

/**
 * A NonEmpty sequence.
 *
 * By definition, this object is guaranteed to have at least one item. The head item is still lazily acquired.
 */
interface NonEmptySequence<A>:
	NonEmptySequenceBase<A>,
	MonadZip<NonEmptySequenceContext, A>,
	Traversable<NonEmptySequenceContext, A>,
	Semigroup<NonEmptySequence<A>> {
	override val scope get() = NonEmptySequence

	override fun <B> map(f: (A) -> B): NonEmptySequence<B> = NonEmptySequence {
		val iterator = iterator()
		NonEmptyIterator(
			f(iterator.head),
			iterator.tail.asSequence().map(f).iterator()
		)
	}

	override fun <B, R> lift2(
		fb: Applicative<NonEmptySequenceContext, B>,
		f: (A, B) -> R,
	) = super<MonadZip>.lift2(fb, f).asNes

	override infix fun <B> bind(f: (A) -> Context<NonEmptySequenceContext, B>): NonEmptySequence<B> =
		flatMap { f(it).asNes }

	fun <B> flatMap(f: (A) -> NonEmptySequence<B>): NonEmptySequence<B> = NonEmptySequence {
		val iterator = iterator()
		val headIterator = f(iterator.head).iterator()
		NonEmptyIterator(
			headIterator.head,
			(headIterator.tail.asSequence() + iterator.tail.asSequence().flatMap(f)).iterator()
		)
	}

	override fun <B, R> zipWith(other: MonadZip<NonEmptySequenceContext, B>, f: (A, B) -> R): NonEmptySequence<R> {
		val otherNes = other.asNes
		return NonEmptySequence {
			val iterator1 = iterator()
			val iterator2 = otherNes.iterator()
			NonEmptyIterator(
				f(iterator1.head, iterator2.head),
				iterator1.tail.asSequence().zip(iterator2.tail.asSequence(), f).iterator()
			)
		}
	}

	override fun <F, B> traverse(
		appScope: Applicative.Scope<F>,
		f: (A) -> Applicative<F, B>,
	): Applicative<F, Traversable<NonEmptySequenceContext, B>> {
		val iterator = iterator()
		return iterator.tail.asSequence().traverse(appScope, f)
			.lift2(f(iterator.head), Sequence<B>::startWithItem)
	}

	override fun combineWith(other: NonEmptySequence<A>) =
		plus(other)

	companion object:
		Monad.Scope<NonEmptySequenceContext>,
		Traversable.Scope<NonEmptySequenceContext> {

		override fun <A> just(a: A) = NonEmptySequence { NonEmptyIterator(a) }
		fun <A> of(head: A, tail: Iterable<A>) =
			NonEmptySequence { NonEmptyIterator(head, tail.iterator()) }

		fun <A> of(head: A, tail: Sequence<A>) =
			NonEmptySequence { NonEmptyIterator(head, tail.iterator()) }
	}
}

interface NonEmptySequenceBase<out A>:
	Sequence<A>,
	Foldable<A> {
	override fun iterator(): NonEmptyIterator<A>

	override fun <R> foldL(initialValue: R, accumulator: (R, A) -> R): R =
		fold(initialValue, accumulator)

	@Deprecated("Non empty sequence always has a first", replaceWith = ReplaceWith("first()"))
	fun firstOrNull(): Nothing = throw UnsupportedOperationException()
	fun first() = iterator().head

	@Deprecated("Non empty sequence always has a last", replaceWith = ReplaceWith("last()"))
	fun lastOrNull(): Nothing = throw UnsupportedOperationException()
	fun last() = iterator().let { it.tail.asSequence().lastOrNull() ?: it.head }

	fun toList(): NonEmptyList<A> = iterator().toNel()

	operator fun plus(element: @UnsafeVariance A): NonEmptySequence<@UnsafeVariance A> = plus(sequenceOf(element))

	operator fun plus(elements: Sequence<@UnsafeVariance A>): NonEmptySequence<@UnsafeVariance A> = NonEmptySequence {
		val iterator = iterator()
		NonEmptyIterator(iterator.head, (iterator.tail.asSequence() + elements).iterator())
	}
}

val <A> Context<NonEmptySequenceContext, A>.asNes get() = this as NonEmptySequence<A>

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

fun <A> nesOf(head: A, vararg tail: A) =
	NonEmptySequence { NonEmptyIterator(head, tail.iterator()) }

fun <A> Sequence<A>.nonEmpty(ifEmpty: NonEmptySequenceBase<A>) = nonEmpty(ifEmpty::iterator)

fun <A> Sequence<A>.nonEmpty(ifEmpty: () -> NonEmptyIterator<A>) =
	NonEmptySequence {
		val iterator = iterator()
		if (iterator.hasNext()) NonEmptyIterator(iterator.next(), iterator)
		else ifEmpty()
	}


