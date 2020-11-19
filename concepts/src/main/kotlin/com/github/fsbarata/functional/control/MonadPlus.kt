package com.github.fsbarata.functional.control

interface MonadPlus<M, out A>: Monad<M, A>, Alternative<M, A> {
	override val scope: Scope<M>

	override fun <B, R> lift2(fb: Applicative<M, B>, f: (A, B) -> R) =
		super<Monad>.lift2(fb, f) as MonadPlus<M, R>

	interface Scope<M>: Monad.Scope<M>, Alternative.Scope<M>
}

fun <M, A> MonadPlus<M, A>.filter(predicate: (A) -> Boolean) =
	bind { if (predicate(it)) scope.just(it) else scope.empty() }
