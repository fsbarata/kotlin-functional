package com.github.fsbarata.functional.control

import com.github.fsbarata.functional.Context
import com.github.fsbarata.functional.data.Functor
import com.github.fsbarata.functional.data.tuple.Tuple2

interface MonadZip<M, out A>: Monad<M, A> {
	fun <B, R> zipWith(other: Context<M, B>, f: (A, B) -> R): MonadZip<M, R>
}

fun <M, A, B, R> zip(monad1: MonadZip<M, A>, monad2: Context<M, B>, f: (A, B) -> R) =
	monad1.zipWith(monad2, f)

fun <M, A, B> zip(monad1: MonadZip<M, A>, monad2: Context<M, B>) = zip(monad1, monad2, ::Pair)
fun <M, A, B> zipT(monad1: MonadZip<M, A>, monad2: Context<M, B>) = zip(monad1, monad2, ::Tuple2)

fun <M, A, B> unzip(zipped: Functor<M, Pair<A, B>>): Pair<Functor<M, A>, Functor<M, B>> =
	Pair(zipped.map { it.first }, zipped.map { it.second })

fun <M, X, Y> unzipT(zipped: Functor<M, Tuple2<X, Y>>): Tuple2<Functor<M, X>, Functor<M, Y>> =
	Tuple2(zipped.map { it.x }, zipped.map { it.y })
