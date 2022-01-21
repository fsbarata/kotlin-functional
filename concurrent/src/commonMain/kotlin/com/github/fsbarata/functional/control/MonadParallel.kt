package com.github.fsbarata.functional.control

import com.github.fsbarata.functional.data.maybe.Optional
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

interface MonadParallel<M, out A>: Monad<M, A> {
	suspend fun <B, C> bindM2(other: Monad<M, B>, f: suspend (A, B) -> Monad<M, C>): Monad<M, C> {
		return coroutineScope {
			val ma = async { bind(scope::just) }
			val mb = async { other.bind(scope::just) }
			ma.await().bind { a -> mb.await().bind { b -> f(a,b) } }
		}
	}
}

suspend fun <A, B, C> Optional<A>.bindM2(other: Optional<B>, f: suspend (A, B) -> Optional<C>) {
	return coroutineScope {
		val ma = async { bind(scope::just) }
		val mb = async { other.bind(scope::just) }
		ma.await().bind { a -> mb.await().bind { b -> f(a,b) } }
	}
}