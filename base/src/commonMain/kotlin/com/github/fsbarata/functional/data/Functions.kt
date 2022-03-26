@file:Suppress("NOTHING_TO_INLINE")

package com.github.fsbarata.functional.data

import kotlin.jvm.JvmName


typealias F0<R> = () -> R
typealias F1<A, R> = (A) -> R
typealias F2<A, B, R> = (A, B) -> R
typealias F3<A, B, C, R> = (A, B, C) -> R
typealias F4<A, B, C, D, R> = (A, B, C, D) -> R

inline fun <A> id(a: A): A = a

inline fun <A> id(): (A) -> A = ::id


inline fun <A, R> compose(crossinline f1: F1<A, R>, crossinline f2: F0<A>): F0<R> = { f1(f2()) }
inline fun <A, B, R> compose(crossinline f1: F1<B, R>, crossinline f2: F1<A, B>): F1<A, R> = { f1(f2(it)) }
inline fun <A, B, C, R> compose(crossinline f1: F2<B, C, R>, crossinline f2: F1<A, B>): F2<A, C, R> =
	{ a, c -> f1(f2(a), c) }

inline fun <A, B, C, R> compose(crossinline f1: F1<C, R>, crossinline f2: F2<A, B, C>): F2<A, B, R> =
	{ a, b -> f1(f2(a, b)) }

inline fun <A, R> composeForward(crossinline f1: F0<A>, crossinline f2: F1<A, R>): F0<R> = compose(f2, f1)
inline fun <A, B, R> composeForward(crossinline f1: F1<A, B>, crossinline f2: F1<B, R>): F1<A, R> = compose(f2, f1)
inline fun <A, B, C, R> composeForward(crossinline f1: F1<A, B>, crossinline f2: F2<B, C, R>): F2<A, C, R> =
	compose(f2, f1)

inline fun <A, B, C, R> composeForward(crossinline f1: F2<A, B, C>, crossinline f2: F1<C, R>): F2<A, B, R> =
	compose(f2, f1)

@JvmName("composeExt")
inline infix fun <A, R> F1<A, R>.compose(crossinline other: F0<A>): F0<R> = compose(this, other)

@JvmName("composeExt")
inline infix fun <A, B, R> F1<B, R>.compose(crossinline other: F1<A, B>): F1<A, R> = compose(this, other)

@JvmName("composeExt")
inline infix fun <A, B, C, R> F2<B, C, R>.compose(crossinline other: F1<A, B>): F2<A, C, R> = compose(this, other)

@JvmName("composeExt")
inline infix fun <A, B, C, R> F1<C, R>.compose(crossinline other: F2<A, B, C>): F2<A, B, R> = compose(this, other)


@JvmName("composeForwardExt")
inline infix fun <A, R> F0<A>.composeForward(crossinline other: F1<A, R>): F0<R> = compose(other, this)

@JvmName("composeForwardExt")
inline infix fun <A, B, R> F1<A, B>.composeForward(crossinline other: F1<B, R>): F1<A, R> = compose(other, this)

@JvmName("composeForwardExt")
inline infix fun <A, B, C, R> F1<A, B>.composeForward(crossinline other: F2<B, C, R>): F2<A, C, R> =
	compose(other, this)

@JvmName("composeForwardExt")
inline infix fun <A, B, C, R> F2<A, B, C>.composeForward(crossinline other: F1<C, R>): F2<A, B, R> =
	compose(other, this)


@JvmName("partialExt")
inline fun <A, R> F1<A, R>.partial(a: A): F0<R> = partial(this, a)
inline fun <A, R> partial(crossinline f: F1<A, R>, a: A): F0<R> = { f(a) }

@JvmName("partial2Ext")
inline fun <A, B, R> F2<A, B, R>.partial2(a: A, b: B): F0<R> = partial2(this, a, b)
inline fun <A, B, R> partial2(crossinline f: F2<A, B, R>, a: A, b: B): F0<R> = { f(a, b) }

@JvmName("partialExt")
inline fun <A, B, R> F2<A, B, R>.partial(a: A): F1<B, R> = partial(this, a)
inline fun <A, B, R> partial(crossinline f: F2<A, B, R>, a: A): F1<B, R> = { f(a, it) }

@JvmName("partialLastExt")
inline fun <A, B, R> F2<A, B, R>.partialLast(b: B): F1<A, R> = partialLast(this, b)
inline fun <A, B, R> partialLast(crossinline f: F2<A, B, R>, b: B): F1<A, R> = { f(it, b) }

@JvmName("partialExt")
inline fun <A, B, C, R> F3<A, B, C, R>.partial(a: A): F2<B, C, R> = partial(this, a)
inline fun <A, B, C, R> partial(crossinline f: F3<A, B, C, R>, a: A): F2<B, C, R> = { b, c -> f(a, b, c) }

@JvmName("partialExt")
inline fun <A, B, C, R> F3<A, B, C, R>.partial2(a: A, b: B): F1<C, R> = partial2(this, a, b)
inline fun <A, B, C, R> partial2(crossinline f: F3<A, B, C, R>, a: A, b: B): F1<C, R> = { c -> f(a, b, c) }

@JvmName("partialExt")
inline fun <A, B, C, R> F3<A, B, C, R>.partial3(a: A, b: B, c: C): F0<R> = partial3(this, a, b, c)
inline fun <A, B, C, R> partial3(crossinline f: F3<A, B, C, R>, a: A, b: B, c: C): F0<R> = { f(a, b, c) }

@JvmName("partialLastExt")
inline fun <A, B, C, R> F3<A, B, C, R>.partialLast(c: C): F2<A, B, R> = partialLast(this, c)
inline fun <A, B, C, R> partialLast(crossinline f: F3<A, B, C, R>, c: C): F2<A, B, R> = { a, b -> f(a, b, c) }

@JvmName("partialLastExt")
inline fun <A, B, C, R> F3<A, B, C, R>.partialLast2(b: B, c: C): F1<A, R> = partialLast2(this, b, c)
inline fun <A, B, C, R> partialLast2(crossinline f: F3<A, B, C, R>, b: B, c: C): F1<A, R> = { a -> f(a, b, c) }

inline fun <A, B, C, D, R> F4<A, B, C, D, R>.partial(a: A): F3<B, C, D, R> = { b, c, d -> invoke(a, b, c, d) }
inline fun <A, B, C, D, R> F4<A, B, C, D, R>.partial2(a: A, b: B): F2<C, D, R> = { c, d -> invoke(a, b, c, d) }
inline fun <A, B, C, D, R> F4<A, B, C, D, R>.partial3(a: A, b: B, c: C): F1<D, R> = { d -> invoke(a, b, c, d) }
inline fun <A, B, C, D, R> F4<A, B, C, D, R>.partial4(a: A, b: B, c: C, d: D): F0<R> = { invoke(a, b, c, d) }
inline fun <A, B, C, D, R> F4<A, B, C, D, R>.partialLast(d: D): F3<A, B, C, R> = { a, b, c -> invoke(a, b, c, d) }
inline fun <A, B, C, D, R> F4<A, B, C, D, R>.partialLast2(c: C, d: D): F2<A, B, R> = { a, b -> invoke(a, b, c, d) }
inline fun <A, B, C, D, R> F4<A, B, C, D, R>.partialLast3(b: B, c: C, d: D): F1<A, R> = { a -> invoke(a, b, c, d) }

@JvmName("curryExt")
inline fun <A, B, R> F2<A, B, R>.curry(): (A) -> (B) -> R = curry(this)
inline fun <A, B, R> curry(crossinline f: F2<A, B, R>): (A) -> (B) -> R = { a -> f.partial(a) }

@JvmName("curryExt")
inline fun <A, B, C, R> F3<A, B, C, R>.curry(): (A) -> (B) -> (C) -> R = curry(this)
inline fun <A, B, C, R> curry(crossinline f: F3<A, B, C, R>): (A) -> (B) -> (C) -> R =
	{ a -> { b -> f.partial2(a, b) } }

@JvmName("uncurryExt")
inline fun <A, B, R> ((A) -> (B) -> R).uncurry(): F2<A, B, R> = uncurry(this)
inline fun <A, B, R> uncurry(crossinline f: ((A) -> (B) -> R)): F2<A, B, R> = { a, b -> f(a).invoke(b) }

@JvmName("uncurryExt")
inline fun <A, B, C, R> ((A) -> (B) -> (C) -> R).uncurry(): F3<A, B, C, R> = uncurry(this)
inline fun <A, B, C, R> uncurry(crossinline f: ((A) -> (B) -> (C) -> R)): F3<A, B, C, R> =
	{ a, b, c -> f(a).invoke(b).invoke(c) }

@JvmName("uncurryExt")
inline fun <A, B, C, R> ((A, B) -> (C) -> R).uncurry(): F3<A, B, C, R> = uncurry(this)
inline fun <A, B, C, R> uncurry(crossinline f: ((A, B) -> (C) -> R)): F3<A, B, C, R> = { a, b, c -> f(a, b).invoke(c) }

@JvmName("flipExt")
inline fun <A, B, R> F2<A, B, R>.flip(): F2<B, A, R> = flip(this)
inline fun <A, B, R> flip(crossinline f: F2<A, B, R>): F2<B, A, R> = { b, a -> f(a, b) }

@JvmName("flipExt")
inline fun <A, B, R> ((A) -> (B) -> R).flip(): (B) -> (A) -> R = flip(this)
inline fun <A, B, R> flip(crossinline f: ((A) -> (B) -> R)): (B) -> (A) -> R = { b -> { a -> f(a).invoke(b) } }

@JvmName("onExt")
inline infix fun <A, B, R> F2<B, B, R>.on(crossinline f: F1<A, B>): (A, A) -> R = on(this, f)
inline fun <A, B, R> on(crossinline f1: F2<B, B, R>, crossinline f2: F1<A, B>): (A, A) -> R =
	{ a1, a2 -> f1(f2(a1), f2(a2)) }

@JvmName("onExt")
inline infix fun <A, B, R> ((B) -> (B) -> R).on(crossinline f: F1<A, B>) = on(this, f)
inline fun <A, B, R> on(crossinline f1: ((B) -> (B) -> R), crossinline f2: F1<A, B>): (A) -> (A) -> R =
	{ a1: A -> val fa1: B = f2(a1); { a2: A -> f1(fa1)(f2(a2)) } }



