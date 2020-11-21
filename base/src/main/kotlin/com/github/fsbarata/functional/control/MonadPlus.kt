package com.github.fsbarata.functional.control

interface MonadPlus<M, out A>: Monad<M, A>, Alternative<M, A> {
	override val scope: Scope<M>

	override fun <B, R> lift2(fb: Applicative<M, B>, f: (A, B) -> R) =
		super<Monad>.lift2(fb, f) as MonadPlus<M, R>

	fun filter(predicate: (A) -> Boolean) =
		filterFromBind(predicate)

	interface Scope<M>: Monad.Scope<M>, Alternative.Scope<M> {
		override fun <A> just(a: A): MonadPlus<M, A>
	}
}

fun <M, A> MonadPlus<M, A>.filterFromBind(predicate: (A) -> Boolean) =
	bind { if (predicate(it)) scope.just(it) else scope.empty() }
