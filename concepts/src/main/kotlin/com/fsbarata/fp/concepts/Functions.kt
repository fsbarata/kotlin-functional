package com.fsbarata.fp.concepts


typealias F0<R> = () -> R
typealias F1<A, R> = (A) -> R
typealias F2<A, B, R> = (A, B) -> R
typealias F3<A, B, C, R> = (A, B, C) -> R

fun <A> id(): (A) -> A = { it }

inline infix fun <B, C, D> F1<B, C>.compose(crossinline other: F1<C, D>): F1<B, D> =
	{ other(this.invoke(it)) }

inline infix fun <B, C, D> F1<C, D>.composeRight(crossinline other: F1<B, C>): F1<B, D> =
	{ invoke(other(it)) }

@Suppress("NOTHING_TO_INLINE")
inline fun <A, R> F1<A, R>.partial(a: A): F0<R> = { invoke(a) }

@Suppress("NOTHING_TO_INLINE")
inline fun <A, B, R> F2<A, B, R>.partial(a: A): F1<B, R> = { invoke(a, it) }

@Suppress("NOTHING_TO_INLINE")
inline fun <A, B, C, R> F3<A, B, C, R>.partial(a: A): F2<B, C, R> = { b, c -> invoke(a, b, c) }

@Suppress("NOTHING_TO_INLINE")
inline fun <A, B, R> F2<A, B, R>.curry(): (A) -> (B) -> R = { a -> partial(a) }

@Suppress("NOTHING_TO_INLINE")
inline fun <A, B, C, R> F3<A, B, C, R>.curry(): (A) -> (B) -> (C) -> R = { a -> partial(a).curry() }
