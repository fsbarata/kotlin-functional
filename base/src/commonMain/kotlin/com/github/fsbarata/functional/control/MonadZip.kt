package com.github.fsbarata.functional.control

import com.github.fsbarata.functional.data.Functor

interface MonadZip<M, out A>: Monad<M, A> {
	fun <B, R> zipWith(other: Functor<M, B>, f: (A, B) -> R): MonadZip<M, R>
}

fun <M, A, B, R> zip(monad1: MonadZip<M, A>, monad2: Functor<M, B>, f: (A, B) -> R) =
	monad1.zipWith(monad2, f)

fun <M, A, B> zip(monad1: MonadZip<M, A>, monad2: Functor<M, B>) =
	zip(monad1, monad2, ::Pair)

fun <M, A, B> unzip(zipped: Functor<M, Pair<A, B>>): Pair<Functor<M, A>, Functor<M, B>> =
	Pair(zipped.map { it.first }, zipped.map { it.second })
