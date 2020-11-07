package com.github.fsbarata.functional.control

interface MonadZip<M, out A>: Monad<M, A> {
	fun <B, R> zipWith(other: MonadZip<M, B>, f: (A, B) -> R): MonadZip<M, R>
}

fun <M, A, B, R> zip(monad1: MonadZip<M, A>, monad2: MonadZip<M, B>, f: (A, B) -> R) =
	monad1.zipWith(monad2, f)

fun <M, A, B> zip(monad1: MonadZip<M, A>, monad2: MonadZip<M, B>) =
	zip(monad1, monad2, ::Pair)

fun <M, A, B> unzip(monad: MonadZip<M, Pair<A, B>>): Pair<Monad<M, A>, Monad<M, B>> =
	Pair(monad.map { it.first }, monad.map { it.second })
