package com.github.fsbarata.functional.data


typealias F0<R> = () -> R
typealias F1<A, R> = (A) -> R
typealias F2<A, B, R> = (A, B) -> R
typealias F3<A, B, C, R> = (A, B, C) -> R

fun <A> id(): (A) -> A = { it }

inline infix fun <B, C, D> F1<B, C>.composeForward(crossinline other: F1<C, D>): F1<B, D> =
	other compose this

inline infix fun <B, C, D> F1<C, D>.compose(crossinline other: F1<B, C>): F1<B, D> =
	{ invoke(other(it)) }

inline infix fun <B, C, D, E> F2<C, D, E>.compose(crossinline other: F1<B, C>): F2<B, D, E> =
	{ b, d -> invoke(other(b), d) }

@Suppress("NOTHING_TO_INLINE")
inline fun <A, R> F1<A, R>.partial(a: A): F0<R> = { invoke(a) }

@Suppress("NOTHING_TO_INLINE")
inline fun <A, B, R> F2<A, B, R>.partial(a: A): F1<B, R> = { invoke(a, it) }

@Suppress("NOTHING_TO_INLINE")
inline fun <A, B, C, R> F3<A, B, C, R>.partial(a: A): F2<B, C, R> = { b, c -> invoke(a, b, c) }

@Suppress("NOTHING_TO_INLINE")
inline fun <A, B, R> F2<A, B, R>.curry(): (A) -> (B) -> R = { a -> partial(a) }

@Suppress("NOTHING_TO_INLINE")
inline fun <A, B, R> ((A) -> (B) -> R).uncurry(): F2<A, B, R> = { a, b -> invoke(a).invoke(b) }

@Suppress("NOTHING_TO_INLINE")
inline fun <A, B, C, R> F3<A, B, C, R>.curry(): (A) -> (B) -> (C) -> R = { a -> partial(a).curry() }

@Suppress("NOTHING_TO_INLINE")
inline fun <A, B, C, R> ((A) -> (B) -> (C) -> R).uncurry(): F3<A, B, C, R> =
	{ a, b, c -> invoke(a).invoke(b).invoke(c) }

@Suppress("NOTHING_TO_INLINE")
inline fun <A, B, C, R> F3<A, B, C, R>.curry2(): (A, B) -> (C) -> R = { a, b -> partial(a).partial(b) }

@Suppress("NOTHING_TO_INLINE")
inline fun <A, B, C, R> ((A, B) -> (C) -> R).uncurry(): F3<A, B, C, R> =
	{ a, b, c -> invoke(a, b).invoke(c) }

@Suppress("NOTHING_TO_INLINE")
inline fun <A, B, R> F2<A, B, R>.flip(): F2<B, A, R> = { b, a -> invoke(a, b) }

fun <A, B, R> on(f1: F2<B, B, R>, f2: F1<A, B>): (A, A) -> R = { a1, a2 -> f1(f2(a1), f2(a2)) }
fun <A, B, R> on(f1: (B) -> (B) -> R, f2: F1<A, B>): (A) -> (A) -> R =
	{ a1 ->
		val f11 = f1(f2(a1))
		val r = { a2: A -> f11(f2(a2)) }
		r
	}

fun <B, C, D> F1<B, C>.first(): F1<Pair<B, D>, Pair<C, D>> =
	{ Pair(invoke(it.first), it.second) }

fun <B, C, D> F1<B, C>.second(): F1<Pair<D, B>, Pair<D, C>> =
	{ Pair(it.first, invoke(it.second)) }

inline infix fun <B, C, D, E> F1<B, C>.split(crossinline other: F1<D, E>): F1<Pair<B, D>, Pair<C, E>> =
	{ Pair(this(it.first), other(it.second)) }

inline infix fun <B, C, D> F1<B, C>.fanout(crossinline other: F1<B, D>): F1<B, Pair<C, D>> =
	{ Pair(this(it), other(it)) }
