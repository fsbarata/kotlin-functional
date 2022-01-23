package com.github.fsbarata.functional.control.monad

import com.github.fsbarata.functional.control.Monad

interface MonadTrans<T, M, out A>: Monad<Monad<M, T>, A> {
	override val scope: Scope<T, M>

	interface Scope<T, M>: Monad.Scope<Monad<M, T>> {
		override fun <A> just(a: A): MonadTrans<T, M, A>
		fun <A> lift(monad: Monad<M, A>): MonadTrans<T, M, A>
	}
}
