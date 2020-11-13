package com.github.fsbarata.functional.control

import com.github.fsbarata.functional.Context
import com.github.fsbarata.functional.data.partial

interface Monad<M, out A>: Applicative<M, A> {
	override val scope: Scope<M>

	infix fun <B> bind(f: (A) -> Context<M, B>): Monad<M, B>

	override fun <B> map(f: (A) -> B): Monad<M, B> = ap(scope.just(f))

	override infix fun <B> ap(ff: Applicative<M, (A) -> B>): Monad<M, B> =
		bind { a -> ff.map { it(a) } }

	override fun <B, R> lift2(fb: Applicative<M, B>, f: (A, B) -> R): Monad<M, R> =
		bind { a -> fb.map(f.partial(a)) }

	interface Scope<C>: Applicative.Scope<C> {
		override fun <A> just(a: A): Monad<C, A>
	}
}
