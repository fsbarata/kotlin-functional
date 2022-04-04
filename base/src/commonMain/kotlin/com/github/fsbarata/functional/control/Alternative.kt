package com.github.fsbarata.functional.control

import com.github.fsbarata.functional.Context
import com.github.fsbarata.functional.data.Functor
import com.github.fsbarata.functional.data.flip
import com.github.fsbarata.functional.data.maybe.Optional
import com.github.fsbarata.functional.data.sequence.NonEmptySequence
import com.github.fsbarata.functional.kotlin.plusElementNe

interface Alternative<F, out A>: Applicative<F, A> {
	override val scope: Scope<F>

	override fun <B, R> lift2(fb: Functor<F, B>, f: (A, B) -> R) =
		super.lift2(fb, f) as Alternative<F, R>

	fun associateWith(other: Context<F, @UnsafeVariance A>): Alternative<F, A>

	fun some(): Alternative<F, NonEmptySequence<@UnsafeVariance A>> =
		lift2(many(), Sequence<A>::plusElementNe.flip())

	fun many(): Alternative<F, Sequence<@UnsafeVariance A>> =
		associate(some(), scope.just(emptySequence()))

	interface Scope<F>: Applicative.Scope<F> {
		fun <A> empty(): Alternative<F, A>
		override fun <A> just(a: A): Alternative<F, A>

		fun <A> fromIterable(iterable: Iterable<A>): Alternative<F, A> =
			iterable.fold(empty()) { r, a -> r.associateWith(just(a)) }

		fun <A> fromSequence(sequence: Sequence<A>): Alternative<F, A> = fromIterable(sequence.asIterable())
		fun <A> fromList(list: List<A>): Alternative<F, A> = fromIterable(list)

		fun <A> fromOptional(optional: Optional<A>): Alternative<F, A> =
			optional.maybe(empty(), ::just)
	}
}

fun <C, A> associate(alt1: Alternative<C, A>, alt2: Context<C, A>) =
	alt1.associateWith(alt2)

