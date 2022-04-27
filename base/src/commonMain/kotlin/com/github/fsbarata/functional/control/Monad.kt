package com.github.fsbarata.functional.control

import com.github.fsbarata.functional.Context
import com.github.fsbarata.functional.data.id
import com.github.fsbarata.functional.data.partial

interface Monad<M, out A>: Applicative<M, A> {
	override val scope: Scope<M>

	infix fun <B> bind(f: (A) -> Context<M, B>): Monad<M, B>

	override fun <B> map(f: (A) -> B): Monad<M, B> =
		bind { scope.just(f(it)) }

	override infix fun <B> ap(ff: Context<M, (A) -> B>): Monad<M, B> =
		(ff as Monad<M, (A) -> B>).bind(::map)

	override fun <B, R> lift2(fb: Context<M, B>, f: (A, B) -> R): Monad<M, R> =
		bind { a -> scope.map(fb, f.partial(a)) }

	interface Scope<M>: Applicative.Scope<M> {
		fun <A, B> bind(ca: Context<M, A>, f: (A) -> Context<M, B>): Context<M, B> =
			(ca as Monad<M, A>).bind(f)
	}
}

@Suppress("UNCHECKED_CAST")
fun <M, A, MA: Monad<M, A>> Monad<M, MA>.flatten() =
	bind(::id) as MA


fun <M, B, MB: Monad<M, B>> Monad<M, *>.andThen(other: MB): MB {
	bind { scope.just(Unit) }
	return other
}
