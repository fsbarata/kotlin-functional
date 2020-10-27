package com.fsbarata.fp.concepts


typealias F1<B, C> = (B) -> C

fun <A> id(): (A) -> A = { it }

inline infix fun <B, C, D> F1<B, C>.compose(crossinline other: F1<C, D>): F1<B, D> =
	{ other(this.invoke(it)) }

inline infix fun <B, C, D> F1<C, D>.composeRight(crossinline other: F1<B, C>): F1<B, D> =
	{ invoke(other(it)) }
