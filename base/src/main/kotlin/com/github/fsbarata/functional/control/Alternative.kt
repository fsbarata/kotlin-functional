package com.github.fsbarata.functional.control

import com.github.fsbarata.functional.Context
import com.github.fsbarata.functional.data.flip
import com.github.fsbarata.functional.data.sequence.NonEmptySequence
import com.github.fsbarata.functional.kotlin.plusElementNes

interface Alternative<F, out A>: Applicative<F, A> {
	override val scope: Scope<F>

	override fun <B, R> lift2(fb: Applicative<F, B>, f: (A, B) -> R) =
		super.lift2(fb, f) as Alternative<F, R>

	fun associateWith(other: Context<F, @UnsafeVariance A>): Alternative<F, A>

	interface Scope<F>: Applicative.Scope<F> {
		fun <A> empty(): Alternative<F, A>

		fun <A> fromList(list: List<A>): Alternative<F, A> =
			list.fold(empty()) { r, a -> r.associateWith(just(a)) }
	}
}

fun <C, A> associate(alt1: Alternative<C, A>, alt2: Context<C, A>) =
	alt1.associateWith(alt2)


fun <F, A> Alternative<F, A>.some(): Alternative<F, NonEmptySequence<A>> =
	lift2(many(), Sequence<A>::plusElementNes.flip())

fun <F, A> Alternative<F, A>.many(): Alternative<F, Sequence<A>> =
	associate(some(), scope.just(emptySequence()))
