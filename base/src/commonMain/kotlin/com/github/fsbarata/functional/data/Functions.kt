@file:Suppress("NOTHING_TO_INLINE")

package com.github.fsbarata.functional.data


typealias F0<R> = () -> R
typealias F1<A, R> = (A) -> R
typealias F2<A, B, R> = (A, B) -> R
typealias F3<A, B, C, R> = (A, B, C) -> R
typealias F4<A, B, C, D, R> = (A, B, C, D) -> R

inline fun <A> id(a: A): A = a

inline fun <A> id(): (A) -> A = ::id


inline infix fun <A, R> F1<A, R>.compose(crossinline other: F0<A>): F0<R> = { invoke(other()) }
inline infix fun <A, B, R> F1<B, R>.compose(crossinline other: F1<A, B>): F1<A, R> = { invoke(other(it)) }
inline infix fun <A, B, C, R> F2<B, C, R>.compose(crossinline other: F1<A, B>): F2<A, C, R> =
	{ a, c -> invoke(other(a), c) }

inline infix fun <A, B, C, R> F1<C, R>.compose(crossinline other: F2<A, B, C>): F2<A, B, R> =
	{ a, b -> invoke(other(a, b)) }

inline infix fun <A, R> F0<A>.composeForward(crossinline other: F1<A, R>): F0<R> = other compose this
inline infix fun <A, B, R> F1<A, B>.composeForward(crossinline other: F1<B, R>): F1<A, R> = other compose this
inline infix fun <A, B, C, R> F1<A, B>.composeForward(crossinline other: F2<B, C, R>): F2<A, C, R> = other compose this
inline infix fun <A, B, C, R> F2<A, B, C>.composeForward(crossinline other: F1<C, R>): F2<A, B, R> = other compose this

inline fun <A, R> F1<A, R>.partial(a: A): F0<R> = { invoke(a) }

inline fun <A, B, R> F2<A, B, R>.partial(a: A, b: B): F0<R> = { invoke(a, b) }
inline fun <A, B, R> F2<A, B, R>.partial(a: A): F1<B, R> = { invoke(a, it) }
inline fun <A, B, R> F2<A, B, R>.partialLast(b: B): F1<A, R> = { invoke(it, b) }

inline fun <A, B, C, R> F3<A, B, C, R>.partial(a: A): F2<B, C, R> = { b, c -> invoke(a, b, c) }
inline fun <A, B, C, R> F3<A, B, C, R>.partial(a: A, b: B): F1<C, R> = { c -> invoke(a, b, c) }
inline fun <A, B, C, R> F3<A, B, C, R>.partial(a: A, b: B, c: C): F0<R> = { invoke(a, b, c) }
inline fun <A, B, C, R> F3<A, B, C, R>.partialLast(c: C): F2<A, B, R> = { a, b -> invoke(a, b, c) }
inline fun <A, B, C, R> F3<A, B, C, R>.partialLast(b: B, c: C): F1<A, R> = { a -> invoke(a, b, c) }

inline fun <A, B, C, D, R> F4<A, B, C, D, R>.partial(a: A): F3<B, C, D, R> = { b, c, d -> invoke(a, b, c, d) }
inline fun <A, B, C, D, R> F4<A, B, C, D, R>.partial(a: A, b: B): F2<C, D, R> = { c, d -> invoke(a, b, c, d) }
inline fun <A, B, C, D, R> F4<A, B, C, D, R>.partial(a: A, b: B, c: C): F1<D, R> = { d -> invoke(a, b, c, d) }
inline fun <A, B, C, D, R> F4<A, B, C, D, R>.partial(a: A, b: B, c: C, d: D): F0<R> = { invoke(a, b, c, d) }
inline fun <A, B, C, D, R> F4<A, B, C, D, R>.partialLast(d: D): F3<A, B, C, R> = { a, b, c -> invoke(a, b, c, d) }
inline fun <A, B, C, D, R> F4<A, B, C, D, R>.partialLast(c: C, d: D): F2<A, B, R> = { a, b -> invoke(a, b, c, d) }
inline fun <A, B, C, D, R> F4<A, B, C, D, R>.partialLast(b: B, c: C, d: D): F1<A, R> = { a -> invoke(a, b, c, d) }

inline fun <A, B, R> F2<A, B, R>.curry(): (A) -> (B) -> R = { a -> partial(a) }
inline fun <A, B, C, R> F3<A, B, C, R>.curry(): (A) -> (B) -> (C) -> R = { a -> { b -> partial(a, b) } }
inline fun <A, B, R> ((A) -> (B) -> R).uncurry(): F2<A, B, R> = { a, b -> invoke(a).invoke(b) }
inline fun <A, B, C, R> ((A) -> (B) -> (C) -> R).uncurry(): F3<A, B, C, R> =
	{ a, b, c -> invoke(a).invoke(b).invoke(c) }

inline fun <A, B, C, R> ((A, B) -> (C) -> R).uncurry(): F3<A, B, C, R> = { a, b, c -> invoke(a, b).invoke(c) }

inline fun <A, B, R> F2<A, B, R>.flip(): F2<B, A, R> = { b, a -> invoke(a, b) }
inline fun <A, B, R> ((A) -> (B) -> R).flip(): (B) -> (A) -> R = { b -> { a -> invoke(a).invoke(b) } }

inline infix fun <A, B, R> F2<B, B, R>.on(crossinline f: F1<A, B>): (A, A) -> R =
	{ a1, a2 -> invoke(f(a1), f(a2)) }

inline infix fun <A, B, R> ((B) -> (B) -> R).on(crossinline f: F1<A, B>) = { a1: A ->
	val fa1: B = f(a1);
	{ a2: A -> invoke(fa1)(f(a2)) }
}



