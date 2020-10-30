package com.fsbarata.fp.concepts


typealias F1<A, R> = (A) -> R
typealias F2<A, B, R> = (A, B) -> R
typealias F3<A, B, C, R> = (A, B, C) -> R

fun <A> id(): (A) -> A = { it }

inline infix fun <B, C, D> F1<B, C>.compose(crossinline other: F1<C, D>): F1<B, D> =
	{ other(this.invoke(it)) }

inline infix fun <B, C, D> F1<C, D>.composeRight(crossinline other: F1<B, C>): F1<B, D> =
	{ invoke(other(it)) }

inline fun <A, B, R> F2<A, B, R>.partial(a: A): F1<B, R> = { invoke(a, it) }
inline fun <A, B, C, R> F3<A, B, C, R>.partial(a: A): F2<B, C, R> = { b, c -> invoke(a, b, c) }
