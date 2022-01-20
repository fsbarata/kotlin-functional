package com.github.fsbarata.functional.control

import com.github.fsbarata.functional.Context
import com.github.fsbarata.functional.data.Functor
import com.github.fsbarata.functional.data.id

interface Monad<M, out A>: Applicative<M, A> {
	override val scope: Scope<M>

	infix fun <B> bind(f: (A) -> Context<M, B>): Monad<M, B>

	override fun <B> map(f: (A) -> B): Monad<M, B> =
		bind { scope.just(f(it)) }

	override infix fun <B> ap(ff: Functor<M, (A) -> B>): Monad<M, B> =
		(ff as Monad<M, (A) -> B>).bind(this::map)

	override fun <B, R> lift2(fb: Functor<M, B>, f: (A, B) -> R): Monad<M, R> =
		bind { a -> fb.map { b -> f(a, b) } }

	interface Scope<C>: Applicative.Scope<C> {
		override fun <A> just(a: A): Monad<C, A>
	}
}

@Suppress("UNCHECKED_CAST")
fun <M, A, MA: Monad<M, A>> Monad<M, MA>.flatten() =
	bind(::id) as MA
