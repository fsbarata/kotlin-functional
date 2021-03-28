package com.github.fsbarata.functional.control

import com.github.fsbarata.functional.Context
import com.github.fsbarata.functional.data.Functor
import com.github.fsbarata.functional.data.compose
import com.github.fsbarata.functional.data.id
import com.github.fsbarata.functional.data.maybe.Optional

interface MonadPlus<M, out A>: Monad<M, A>, Alternative<M, A> {
	override val scope: Scope<M>

	override fun <B, R> lift2(fb: Functor<M, B>, f: (A, B) -> R) =
		super<Monad>.lift2(fb, f) as MonadPlus<M, R>

	override infix fun <B> bind(f: (A) -> Context<M, B>): MonadPlus<M, B>

	fun filter(predicate: (A) -> Boolean) =
		filterFromBind(predicate)

	fun partition(predicate: (A) -> Boolean): Pair<MonadPlus<M, A>, MonadPlus<M, A>> =
		Pair(
			filter(predicate),
			filter(Boolean::not compose predicate)
		)

	fun <B: Any> mapNotNull(f: (A) -> B?) =
		bind { a -> scope.just(f(a) ?: return@bind scope.empty()) }

	fun <B: Any> mapNotNone(f: (A) -> Optional<B>) =
		mapNotNull { f(it).orNull() }

	interface Scope<M>: Monad.Scope<M>, Alternative.Scope<M> {
		override fun <A> just(a: A): MonadPlus<M, A>
	}
}

fun <M, A> MonadPlus<M, A>.filterFromBind(predicate: (A) -> Boolean) =
	bind { if (predicate(it)) scope.just(it) else scope.empty() }

fun <M, A: Any> MonadPlus<M, A?>.filterNotNull() = mapNotNull(id())
fun <M, A: Any> MonadPlus<M, Optional<A>>.filterPresent() = mapNotNone(id())
