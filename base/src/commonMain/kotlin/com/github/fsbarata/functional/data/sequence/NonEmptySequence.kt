@file:Suppress("NOTHING_TO_INLINE")

package com.github.fsbarata.functional.data.sequence

import com.github.fsbarata.functional.Context
import com.github.fsbarata.functional.control.Applicative
import com.github.fsbarata.functional.control.Monad
import com.github.fsbarata.functional.control.MonadZip
import com.github.fsbarata.functional.data.Foldable
import com.github.fsbarata.functional.data.Functor
import com.github.fsbarata.functional.data.Semigroup
import com.github.fsbarata.functional.data.Traversable
import com.github.fsbarata.functional.data.list.NonEmptyList
import com.github.fsbarata.functional.data.set.NonEmptySet
import com.github.fsbarata.functional.utils.*

internal typealias NonEmptySequenceContext = NonEmptySequence<*>

/**
 * A NonEmpty sequence.
 *
 * By definition, this object is guaranteed to have at least one item. The head item is still lazily acquired.
 */
class NonEmptySequence<A> internal constructor(private val iter: () -> Iterator<A>):
	NonEmptySequenceBase<A>,
	MonadZip<NonEmptySequenceContext, A>,
	Traversable<NonEmptySequenceContext, A>,
	Semigroup<NonEmptySequence<A>> {
	override val scope get() = NonEmptySequence

	override fun iterator(): Iterator<A> = iter()

	override fun <B> map(f: (A) -> B): NonEmptySequence<B> = NonEmptySequence {
		val iterator = iterator()
		nonEmptyIterator(f(iterator.next()), iterator.asSequence().map(f).iterator())
	}

	override fun <B, R> lift2(
		fb: Functor<NonEmptySequenceContext, B>,
		f: (A, B) -> R,
	) = super.lift2(fb, f).asNes

	override infix fun <B> bind(f: (A) -> Context<NonEmptySequenceContext, B>): NonEmptySequence<B> =
		flatMap { f(it).asNes }

	fun <B> flatMap(f: (A) -> NonEmptySequence<B>): NonEmptySequence<B> = NonEmptySequence {
		val iterator = iterator()
		val headIterator = f(iterator.next()).iterator()
		nonEmptyIterator(
			headIterator.next(),
			(headIterator.asSequence() + iterator.asSequence().flatMap(f)).iterator(),
		)
	}

	override fun <B, R> zipWith(other: Functor<NonEmptySequenceContext, B>, f: (A, B) -> R): NonEmptySequence<R> {
		val otherNes = other.asNes
		return NonEmptySequence {
			val iterator1 = iterator()
			val iterator2 = otherNes.iterator()
			nonEmptyIterator(
				f(iterator1.next(), iterator2.next()),
				iterator1.asSequence().zip(iterator2.asSequence(), f).iterator(),
			)
		}
	}

	override fun <F, B> traverse(
		appScope: Applicative.Scope<F>,
		f: (A) -> Functor<F, B>,
	): Functor<F, Traversable<NonEmptySequenceContext, B>> {
		val iterator = iterator()
		val head = iterator.next()
		return appScope.lift2(
			iterator.asSequence().traverse(appScope, f),
			f(head),
			Sequence<B>::startWithItem
		)
	}

	override fun combineWith(other: NonEmptySequence<A>) =
		plus(other)

	companion object:
		Monad.Scope<NonEmptySequenceContext>,
		Traversable.Scope<NonEmptySequenceContext> {

		override fun <A> just(a: A) =
			NonEmptySequence { singleItemIterator(a) }

		fun <A> of(head: A, tail: Iterable<A>) =
			NonEmptySequence { nonEmptyIterator(head, tail.iterator()) }

		fun <A> of(head: A, tail: Sequence<A>) =
			NonEmptySequence { nonEmptyIterator(head, tail.iterator()) }
	}
}

interface NonEmptySequenceBase<out A>:
	Sequence<A>,
	Foldable<A> {
	override fun <R> foldL(initialValue: R, accumulator: (R, A) -> R): R =
		fold(initialValue, accumulator)

	@Deprecated("Non empty sequence always has a first", replaceWith = ReplaceWith("first()"))
	fun firstOrNull(): Nothing = throw UnsupportedOperationException()
	fun first() = iterator().next()

	@Deprecated("Non empty sequence always has a last", replaceWith = ReplaceWith("last()"))
	fun lastOrNull(): Nothing = throw UnsupportedOperationException()
	fun last() = iterator().asSequence().last()

	fun toList(): NonEmptyList<A> = iterator().toNelUnsafe()
	fun toSet(): NonEmptySet<A> = iterator().toNesUnsafe()

	operator fun plus(element: @UnsafeVariance A): NonEmptySequence<@UnsafeVariance A> =
		NonEmptySequence { nonEmptyIterator(iterator(), element) }

	operator fun plus(elements: Sequence<@UnsafeVariance A>): NonEmptySequence<@UnsafeVariance A> = NonEmptySequence {
		val iterator = iterator()
		nonEmptyIterator(iterator.next(), (iterator.asSequence() + elements).iterator())
	}
}

val <A> Context<NonEmptySequenceContext, A>.asNes get() = this as NonEmptySequence<A>

fun <A: Any> nonEmptySequence(initialFunction: () -> A, nextFunction: (A) -> A?) =
	NonEmptySequence {
		val head = initialFunction()
		nonEmptyIterator(head, generateSequence(nextFunction(head), nextFunction).iterator())
	}

fun <A: Any> nonEmptySequence(head: A, nextFunction: (A) -> A?) =
	NonEmptySequence { nonEmptyIterator(head, generateSequence(nextFunction(head), nextFunction).iterator()) }

inline fun <A> Sequence<A>.startWithItem(item: A) =
	NonEmptySequence.of(item, this)

fun <A> nonEmptySequenceOf(head: A, vararg tail: A) =
	NonEmptySequence { nonEmptyIterator(head, tail.iterator()) }

fun <A> Sequence<A>.nonEmpty(ifEmpty: NonEmptySequenceBase<A>) =
	nonEmpty { ifEmpty }

fun <A> Sequence<A>.nonEmpty(ifEmpty: () -> NonEmptySequenceBase<A>) =
	NonEmptySequence {
		val iterator = iterator()
		val nonEmptyIterator = if (iterator.hasNext()) iterator else ifEmpty().iterator()
		nonEmptyIterator(nonEmptyIterator.next(), nonEmptyIterator)
	}


