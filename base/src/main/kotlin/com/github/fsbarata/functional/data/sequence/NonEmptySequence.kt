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
interface NonEmptySequence<A>:
	NonEmptySequenceBase<A>,
	MonadZip<NonEmptySequenceContext, A>,
	Traversable<NonEmptySequenceContext, A>,
	Semigroup<NonEmptySequence<A>> {
	override val scope get() = NonEmptySequence

	override fun <B> map(f: (A) -> B): NonEmptySequence<B> = headSequence {
		val iterator = iterator()
		f(iterator.next()) then iterator.asSequence().map(f).iterator()
	}

	override fun <B, R> lift2(
		fb: Functor<NonEmptySequenceContext, B>,
		f: (A, B) -> R,
	) = super.lift2(fb, f).asNes

	override infix fun <B> bind(f: (A) -> Context<NonEmptySequenceContext, B>): NonEmptySequence<B> =
		flatMap { f(it).asNes }

	fun <B> flatMap(f: (A) -> NonEmptySequence<B>): NonEmptySequence<B> = headSequence {
		val iterator = iterator()
		val headIterator = f(iterator.next()).iterator()
		headIterator.next() then
				(headIterator.asSequence() + iterator.asSequence().flatMap(f)).iterator()
	}

	override fun <B, R> zipWith(other: MonadZip<NonEmptySequenceContext, B>, f: (A, B) -> R): NonEmptySequence<R> {
		val otherNes = other.asNes
		return headSequence {
			val iterator1 = iterator()
			val iterator2 = otherNes.iterator()
			f(iterator1.next(), iterator2.next()) then
					iterator1.asSequence().zip(iterator2.asSequence(), f).iterator()
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
			headSequence { a then EmptyIterator }

		fun <A> of(head: A, tail: Iterable<A>) =
			headSequence { head then tail.iterator() }

		fun <A> of(head: A, tail: Sequence<A>) =
			headSequence { head then tail.iterator() }
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
		tailSequence { iterator() then element }

	operator fun plus(elements: Sequence<@UnsafeVariance A>): NonEmptySequence<@UnsafeVariance A> = headSequence {
		val iterator = iterator()
		iterator.next() then (iterator.asSequence() + elements).iterator()
	}
}

val <A> Context<NonEmptySequenceContext, A>.asNes get() = this as NonEmptySequence<A>

internal class NonEmptySequenceBuilder {
	infix fun <A, B> A.then(b: B) = this to b
}
internal fun <A> headSequence(f: NonEmptySequenceBuilder.() -> Pair<A, Iterator<A>>): NonEmptySequence<A> {
	val builder = NonEmptySequenceBuilder()
	return object: NonEmptySequence<A> {
		override fun iterator(): Iterator<A> {
			val (head, tail) = builder.f()
			return nonEmptyIterator(head, tail)
		}
	}
}

internal fun <A> tailSequence(f: NonEmptySequenceBuilder.() -> Pair<Iterator<A>, A>): NonEmptySequence<A> {
	val builder = NonEmptySequenceBuilder()
	return object: NonEmptySequence<A> {
		override fun iterator(): Iterator<A> {
			val (head, tail) = builder.f()
			return nonEmptyIterator(head, tail)
		}
	}
}

fun <A: Any> nonEmptySequence(head: A, nextFunction: (A) -> A?) = headSequence {
	Pair(
		head,
		generateSequence(nextFunction(head), nextFunction).iterator()
	)
}

fun <A> Sequence<A>.startWithItem(item: A) =
	headSequence { item then iterator() }

fun <A> nonEmptySequenceOf(head: A, vararg tail: A) =
	headSequence { head then tail.iterator() }

fun <A> Sequence<A>.nonEmpty(ifEmpty: NonEmptySequenceBase<A>) = nonEmpty { ifEmpty }

fun <A> Sequence<A>.nonEmpty(ifEmpty: () -> NonEmptySequenceBase<A>) =
	headSequence {
		val iterator = iterator().takeIf { it.hasNext() } ?: ifEmpty().iterator()
		iterator.next() then iterator
	}


