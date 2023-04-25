package com.github.fsbarata.functional.control

import com.github.fsbarata.functional.Context
import com.github.fsbarata.functional.data.flip
import com.github.fsbarata.functional.data.list.ListF
import com.github.fsbarata.functional.data.list.NonEmptyList
import com.github.fsbarata.functional.data.list.startWithItem
import com.github.fsbarata.functional.data.maybe.Optional

interface Alternative<F, out A>: Applicative<F, A> {
	override val scope: Scope<F>

	override fun <B, R> lift2(fb: Context<F, B>, f: (A, B) -> R) =
		super.lift2(fb, f) as Alternative<F, R>

	fun combineWith(other: Context<F, @UnsafeVariance A>): Alternative<F, A>

	fun some(): Alternative<F, NonEmptyList<A>> =
		lift2(many(), List<A>::startWithItem.flip())

	fun many(): Alternative<F, List<A>> =
		combine(some(), scope.just(ListF.empty()))

	interface Scope<F>: Applicative.Scope<F> {
		fun <A> empty(): Context<F, A>

		fun <A> combine(fa1: Context<F, A>, fa2: Context<F, A>): Context<F, A> =
			(fa1 as Alternative<F, A>).combineWith(fa2)

		fun <A> fromIterable(iterable: Iterable<A>): Context<F, A> =
			iterable.fold(empty()) { r, a -> combine(r, just(a)) }

		fun <A> fromSequence(sequence: Sequence<A>): Context<F, A> = fromIterable(sequence.asIterable())
		fun <A> fromList(list: List<A>): Context<F, A> = fromIterable(list)

		fun <A> fromOptional(optional: Optional<A>): Context<F, A> =
			optional.maybe(empty(), ::just)

		fun <A> some(ca: Context<F, A>): Context<F, NonEmptyList<A>> =
			if (ca is Alternative) ca.some()
			else lift2(ca, many(ca), List<A>::startWithItem.flip())

		fun <A> many(ca: Context<F, A>): Context<F, List<A>> =
			if (ca is Alternative) ca.many()
			else combine(some(ca), just(ListF.empty()))
	}
}

fun <C, A> combine(alt1: Alternative<C, A>, alt2: Context<C, A>): Alternative<C, A> =
	alt1.combineWith(alt2)
