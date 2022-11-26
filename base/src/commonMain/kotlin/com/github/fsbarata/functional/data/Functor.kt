package com.github.fsbarata.functional.data

import com.github.fsbarata.functional.Context
import com.github.fsbarata.functional.control.Lift1

interface Functor<F, out A>: Invariant<F, A> {
	fun <B> map(f: (A) -> B): Functor<F, B>

	override fun <B> invmap(f: (A) -> B, g: (B) -> @UnsafeVariance A): Functor<F, B> = map(f)

	fun onEach(f: (A) -> Unit): Functor<F, A> = map { a -> f(a); a }

	interface Scope<F>: Invariant.Scope<F> {
		fun <A, B> map(ca: Context<F, A>, f: (A) -> B): Context<F, B> =
			(ca as Functor<F, A>).map(f)

		fun <A> onEach(ca: Context<F, A>, f: (A) -> Unit): Context<F, A> =
			if (ca is Functor) ca.onEach(f)
			else map(ca) { a -> f(a); a }

		override fun <A, B> invmap(ca: Context<F, A>, f: (A) -> B, g: (B) -> A): Context<F, B> =
			if (ca is Invariant) ca.invmap(f, g)
			else map(ca, f)
	}
}

fun <A> liftOnEach(f: (A) -> Unit): Lift1<A, A> = Lift1 { a -> f(a); a }

fun <F> Functor<F, *>.functorScope() = object: Functor.Scope<F> {}