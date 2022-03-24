package com.github.fsbarata.functional.data.tuple

inline fun <A, B, R> Pair<A, B>.mapLeft(f: (A) -> R): Pair<R, B> = Pair(f(first), second)
inline fun <A, B, R> Pair<A, B>.map(f: (B) -> R): Pair<A, R> = Pair(first, f(second))
inline fun <A, B, C, D> Pair<A, B>.bimap(f: (A) -> C, g: (B) -> D): Pair<C, D> = Pair(f(first), g(second))

inline fun <A, B, R> Pair<A, B>.coflatMap(f: (Pair<A, B>) -> R): Pair<A, R> = Pair(first, f(this))
inline fun <A, B> Pair<A, B>.duplicate(): Pair<A, Pair<A, B>> = Pair(first, this)

fun <A, B> Pair<A, B>.swap(): Pair<B, A> = Pair(second, first)
