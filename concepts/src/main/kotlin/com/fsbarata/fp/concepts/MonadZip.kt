package com.fsbarata.fp.concepts

interface MonadZip<C, out A>: Monad<C, A> {
	fun <B, R> zipWith(other: MonadZip<C, B>, f: (A, B) -> R): MonadZip<C, R>
}

fun <C, A, B, R> zip(monad1: MonadZip<C, A>, monad2: MonadZip<C, B>, f: (A, B) -> R) =
	monad1.zipWith(monad2, f)

fun <C, A, B> zip(monad1: MonadZip<C, A>, monad2: MonadZip<C, B>) =
	zip(monad1, monad2, ::Pair)

fun <C, A, B> unzip(monad: MonadZip<C, Pair<A, B>>): Pair<Monad<C, A>, Monad<C, B>> =
	Pair(monad.map { it.first }, monad.map { it.second })
