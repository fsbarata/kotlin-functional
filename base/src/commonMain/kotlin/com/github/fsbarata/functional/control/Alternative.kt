package com.github.fsbarata.functional.control

import com.github.fsbarata.functional.Context
import com.github.fsbarata.functional.data.Functor
import com.github.fsbarata.functional.data.flip
import com.github.fsbarata.functional.data.maybe.Optional
import com.github.fsbarata.functional.data.sequence.NonEmptySequence
import com.github.fsbarata.functional.kotlin.plusElementNe

interface Alternative<F, out A>: Applicative<F, A> {
	override val scope: Scope<F>

	override fun <B, R> lift2(fb: Context<F, B>, f: (A, B) -> R) =
		super.lift2(fb, f) as Alternative<F, R>

	fun combineWith(other: Context<F, @UnsafeVariance A>): Alternative<F, A>

	fun some(): Alternative<F, NonEmptySequence<@UnsafeVariance A>> =
		lift2(many(), Sequence<A>::plusElementNe.flip())

	fun many(): Alternative<F, Sequence<@UnsafeVariance A>> =
		combine(some(), scope.just(emptySequence()))

	interface Scope<F>: Applicative.Scope<F> {
		fun <A> empty(): Context<F, A>

		fun <A> fromIterable(iterable: Iterable<A>): Context<F, A> =
			iterable.fold(empty()) { r, a -> combine(r, just(a)) }

		fun <A> combine(item1: Context<F, A>, item2: Context<F, A>): Context<F, A> =
			(item1 as Alternative<F, A>).combineWith(item2)

		fun <A> fromSequence(sequence: Sequence<A>): Context<F, A> = fromIterable(sequence.asIterable())
		fun <A> fromList(list: List<A>): Context<F, A> = fromIterable(list)

		fun <A> fromOptional(optional: Optional<A>): Context<F, A> =
			optional.maybe(empty(), ::just)
	}
}

fun <C, A> combine(alt1: Alternative<C, A>, alt2: Context<C, A>): Alternative<C, A> =
	alt1.combineWith(alt2)

