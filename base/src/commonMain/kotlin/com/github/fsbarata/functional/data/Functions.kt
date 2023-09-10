@file:Suppress("NOTHING_TO_INLINE")

package com.github.fsbarata.functional.data

import kotlin.jvm.JvmName


typealias F0<R> = () -> R
typealias sF0<R> = suspend () -> R
typealias F1<A, R> = (A) -> R
typealias sF1<A, R> = suspend (A) -> R
typealias F2<A, B, R> = (A, B) -> R
typealias sF2<A, B, R> = suspend (A, B) -> R
typealias F3<A, B, C, R> = (A, B, C) -> R
typealias sF3<A, B, C, R> = suspend (A, B, C) -> R
typealias F4<A, B, C, D, R> = (A, B, C, D) -> R
typealias sF4<A, B, C, D, R> = suspend (A, B, C, D) -> R

inline fun <A> id(a: A): A = a

fun <A> void(f: F1<A, *>): (A) -> Unit = { a: A -> f(a) }
fun <A, B> void(f: F2<A, B, *>): (A, B) -> Unit = { a: A, b: B -> f(a, b) }
fun <A, B, C> void(f: F3<A, B, C, *>): (A, B, C) -> Unit = { a: A, b: B, c: C -> f(a, b, c) }
fun <A, B, C, D> void(f: F4<A, B, C, D, *>): (A, B, C, D) -> Unit = { a: A, b: B, c: C, d: D -> f(a, b, c, d) }

inline fun <A, R> compose0(crossinline f1: F1<A, R>, crossinline f2: F0<A>): F0<R> = { f1(f2()) }
inline fun <A, R> composeS0(crossinline f1: sF1<A, R>, crossinline f2: sF0<A>): sF0<R> = { f1(f2()) }
inline fun <A, B, R> compose(crossinline f1: F1<B, R>, crossinline f2: F1<A, B>): F1<A, R> = { f1(f2(it)) }
inline fun <A, B, R> composeS(crossinline f1: sF1<B, R>, crossinline f2: sF1<A, B>): sF1<A, R> = { f1(f2(it)) }

inline fun <A, B, C, R> compose2(crossinline f1: F1<C, R>, crossinline f2: F2<A, B, C>): F2<A, B, R> =
	{ a, b -> f1(f2(a, b)) }

inline fun <A, B, C, R> composeS2(crossinline f1: sF1<C, R>, crossinline f2: sF2<A, B, C>): sF2<A, B, R> =
	{ a, b -> f1(f2(a, b)) }

inline fun <A, B, R> compose0(crossinline f1: F2<A, B, R>, crossinline f2: F0<A>): F1<B, R> = { b -> f1(f2(), b) }
inline fun <A, B, R> composeS0(crossinline f1: sF2<A, B, R>, crossinline f2: sF0<A>): sF1<B, R> =
	{ b -> f1(f2(), b) }

inline fun <A, B, C, R> compose(crossinline f1: F2<B, C, R>, crossinline f2: F1<A, B>): F2<A, C, R> =
	{ a, c -> f1(f2(a), c) }

inline fun <A, B, C, R> composeS(crossinline f1: sF2<B, C, R>, crossinline f2: sF1<A, B>): sF2<A, C, R> =
	{ a, c -> f1(f2(a), c) }

inline fun <A, R> composeForward(crossinline f1: F0<A>, crossinline f2: F1<A, R>): F0<R> = compose0(f2, f1)
inline fun <A, R> composeForwardS(crossinline f1: sF0<A>, crossinline f2: sF1<A, R>): sF0<R> =
	composeS0(f2, f1)

inline fun <A, B, R> composeForward(crossinline f1: F1<A, B>, crossinline f2: F1<B, R>): F1<A, R> = compose(f2, f1)
inline fun <A, B, R> composeForwardS(crossinline f1: sF1<A, B>, crossinline f2: sF1<B, R>): sF1<A, R> =
	composeS(f2, f1)

inline fun <A, B, C, R> composeForward(crossinline f1: F2<A, B, C>, crossinline f2: F1<C, R>): F2<A, B, R> =
	compose2(f2, f1)

inline fun <A, B, C, R> composeForwardS(crossinline f1: sF2<A, B, C>, crossinline f2: sF1<C, R>): sF2<A, B, R> =
	composeS2(f2, f1)

inline fun <A, B, R> composeForward2(crossinline f1: F0<A>, crossinline f2: F2<A, B, R>): F1<B, R> = compose0(f2, f1)
inline fun <A, B, R> composeForwardS2(crossinline f1: sF0<A>, crossinline f2: sF2<A, B, R>): sF1<B, R> =
	composeS0(f2, f1)

inline fun <A, B, C, R> composeForward2(crossinline f1: F1<A, B>, crossinline f2: F2<B, C, R>): F2<A, C, R> =
	compose(f2, f1)

inline fun <A, B, C, R> composeForwardS2(crossinline f1: sF1<A, B>, crossinline f2: sF2<B, C, R>): sF2<A, C, R> =
	composeS(f2, f1)


@JvmName("compose0Ext")
inline infix fun <A, R> F1<A, R>.compose0(crossinline other: F0<A>): F0<R> = compose0(this, other)

@JvmName("composeExt")
inline infix fun <A, B, R> F1<B, R>.compose(crossinline other: F1<A, B>): F1<A, R> = compose(this, other)

@JvmName("compose2Ext")
inline infix fun <A, B, C, R> F1<C, R>.compose2(crossinline other: F2<A, B, C>): F2<A, B, R> = compose2(this, other)

@JvmName("compose0Ext")
inline fun <A, B, R> F2<A, B, R>.compose0(crossinline other: F0<A>): F1<B, R> = compose0(this, other)

@JvmName("composeExt")
inline infix fun <A, B, C, R> F2<B, C, R>.compose(crossinline other: F1<A, B>): F2<A, C, R> = compose(this, other)


@JvmName("composeForwardExt")
inline infix fun <A, R> F0<A>.composeForward(crossinline other: F1<A, R>): F0<R> = composeForward(this, other)

@JvmName("composeForwardExt")
inline infix fun <A, B, R> F1<A, B>.composeForward(crossinline other: F1<B, R>): F1<A, R> = composeForward(this, other)

@JvmName("composeForwardExt")
inline infix fun <A, B, C, R> F2<A, B, C>.composeForward(crossinline other: F1<C, R>): F2<A, B, R> =
	composeForward(this, other)

@JvmName("composeForward2Ext")
inline fun <A, B, R> F0<A>.composeForward2(crossinline other: F2<A, B, R>): F1<B, R> = composeForward2(this, other)

@JvmName("composeForward2Ext")
inline infix fun <A, B, C, R> F1<A, B>.composeForward2(crossinline other: F2<B, C, R>): F2<A, C, R> =
	composeForward2(this, other)


@JvmName("partialExt")
inline fun <A, R> F1<A, R>.partial(a: A): F0<R> = partial(this, a)
inline fun <A, R> partial(crossinline f: F1<A, R>, a: A): F0<R> = { f(a) }
inline fun <A, R> partialS(crossinline f: sF1<A, R>, a: A): sF0<R> = { f(a) }

@JvmName("partial2Ext")
inline fun <A, B, R> F2<A, B, R>.partial2(a: A, b: B): F0<R> = partial2(this, a, b)
inline fun <A, B, R> partial2(crossinline f: F2<A, B, R>, a: A, b: B): F0<R> = { f(a, b) }
inline fun <A, B, R> partialS2(crossinline f: sF2<A, B, R>, a: A, b: B): sF0<R> = { f(a, b) }

@JvmName("partialExt")
inline fun <A, B, R> F2<A, B, R>.partial(a: A): F1<B, R> = partial(this, a)
inline fun <A, B, R> partial(crossinline f: F2<A, B, R>, a: A): F1<B, R> = { f(a, it) }
inline fun <A, B, R> partialS(crossinline f: sF2<A, B, R>, a: A): sF1<B, R> = { f(a, it) }

@JvmName("partialLastExt")
inline fun <A, B, R> F2<A, B, R>.partialLast(b: B): F1<A, R> = partialLast(this, b)
inline fun <A, B, R> partialLast(crossinline f: F2<A, B, R>, b: B): F1<A, R> = { f(it, b) }
inline fun <A, B, R> partialLastS(crossinline f: sF2<A, B, R>, b: B): sF1<A, R> = { f(it, b) }

@JvmName("partialExt")
inline fun <A, B, C, R> F3<A, B, C, R>.partial(a: A): F2<B, C, R> = partial(this, a)
inline fun <A, B, C, R> partial(crossinline f: F3<A, B, C, R>, a: A): F2<B, C, R> = { b, c -> f(a, b, c) }
inline fun <A, B, C, R> partialS(crossinline f: sF3<A, B, C, R>, a: A): sF2<B, C, R> = { b, c -> f(a, b, c) }

@JvmName("partial2Ext")
inline fun <A, B, C, R> F3<A, B, C, R>.partial2(a: A, b: B): F1<C, R> = partial2(this, a, b)
inline fun <A, B, C, R> partial2(crossinline f: F3<A, B, C, R>, a: A, b: B): F1<C, R> = { c -> f(a, b, c) }
inline fun <A, B, C, R> partialS2(crossinline f: sF3<A, B, C, R>, a: A, b: B): sF1<C, R> = { c -> f(a, b, c) }

@JvmName("partial3Ext")
inline fun <A, B, C, R> F3<A, B, C, R>.partial3(a: A, b: B, c: C): F0<R> = partial3(this, a, b, c)
inline fun <A, B, C, R> partial3(crossinline f: F3<A, B, C, R>, a: A, b: B, c: C): F0<R> = { f(a, b, c) }
inline fun <A, B, C, R> partialS3(crossinline f: sF3<A, B, C, R>, a: A, b: B, c: C): sF0<R> = { f(a, b, c) }

@JvmName("partialLastExt")
inline fun <A, B, C, R> F3<A, B, C, R>.partialLast(c: C): F2<A, B, R> = partialLast(this, c)
inline fun <A, B, C, R> partialLast(crossinline f: F3<A, B, C, R>, c: C): F2<A, B, R> = { a, b -> f(a, b, c) }
inline fun <A, B, C, R> partialLastS(crossinline f: sF3<A, B, C, R>, c: C): sF2<A, B, R> = { a, b -> f(a, b, c) }

@JvmName("partialLast2Ext")
inline fun <A, B, C, R> F3<A, B, C, R>.partialLast2(b: B, c: C): F1<A, R> = partialLast2(this, b, c)
inline fun <A, B, C, R> partialLast2(crossinline f: F3<A, B, C, R>, b: B, c: C): F1<A, R> = { a -> f(a, b, c) }
inline fun <A, B, C, R> partialLastS2(crossinline f: sF3<A, B, C, R>, b: B, c: C): sF1<A, R> = { a -> f(a, b, c) }

@JvmName("partialExt")
inline fun <A, B, C, D, R> F4<A, B, C, D, R>.partial(a: A): F3<B, C, D, R> = partial(this, a)
inline fun <A, B, C, D, R> partial(crossinline f: F4<A, B, C, D, R>, a: A): F3<B, C, D, R> =
	{ b, c, d -> f(a, b, c, d) }

inline fun <A, B, C, D, R> partialS(crossinline f: sF4<A, B, C, D, R>, a: A): sF3<B, C, D, R> =
	{ b, c, d -> f(a, b, c, d) }

@JvmName("partial2Ext")
inline fun <A, B, C, D, R> F4<A, B, C, D, R>.partial2(a: A, b: B): F2<C, D, R> = partial2(this, a, b)
inline fun <A, B, C, D, R> partial2(crossinline f: F4<A, B, C, D, R>, a: A, b: B): F2<C, D, R> =
	{ c, d -> f(a, b, c, d) }

inline fun <A, B, C, D, R> partialS2(crossinline f: sF4<A, B, C, D, R>, a: A, b: B): sF2<C, D, R> =
	{ c, d -> f(a, b, c, d) }

@JvmName("partial3Ext")
inline fun <A, B, C, D, R> F4<A, B, C, D, R>.partial3(a: A, b: B, c: C): F1<D, R> = partial3(this, a, b, c)
inline fun <A, B, C, D, R> partial3(crossinline f: F4<A, B, C, D, R>, a: A, b: B, c: C): F1<D, R> =
	{ d -> f(a, b, c, d) }

inline fun <A, B, C, D, R> partialS3(crossinline f: sF4<A, B, C, D, R>, a: A, b: B, c: C): sF1<D, R> =
	{ d -> f(a, b, c, d) }

@JvmName("partial4Ext")
inline fun <A, B, C, D, R> F4<A, B, C, D, R>.partial4(a: A, b: B, c: C, d: D): F0<R> = partial4(this, a, b, c, d)
inline fun <A, B, C, D, R> partial4(crossinline f: F4<A, B, C, D, R>, a: A, b: B, c: C, d: D): F0<R> = { f(a, b, c, d) }
inline fun <A, B, C, D, R> partialS4(crossinline f: sF4<A, B, C, D, R>, a: A, b: B, c: C, d: D): sF0<R> =
	{ f(a, b, c, d) }

@JvmName("partialLastExt")
inline fun <A, B, C, D, R> F4<A, B, C, D, R>.partialLast(d: D): F3<A, B, C, R> = partialLast(this, d)
inline fun <A, B, C, D, R> partialLast(crossinline f: F4<A, B, C, D, R>, d: D): F3<A, B, C, R> =
	{ a, b, c -> f(a, b, c, d) }

inline fun <A, B, C, D, R> partialLastS(crossinline f: sF4<A, B, C, D, R>, d: D): sF3<A, B, C, R> =
	{ a, b, c -> f(a, b, c, d) }

@JvmName("partialLast2Ext")
inline fun <A, B, C, D, R> F4<A, B, C, D, R>.partialLast2(c: C, d: D): F2<A, B, R> = partialLast2(this, c, d)
inline fun <A, B, C, D, R> partialLast2(crossinline f: F4<A, B, C, D, R>, c: C, d: D): F2<A, B, R> =
	{ a, b -> f(a, b, c, d) }

inline fun <A, B, C, D, R> partialLastS2(crossinline f: sF4<A, B, C, D, R>, c: C, d: D): sF2<A, B, R> =
	{ a, b -> f(a, b, c, d) }

@JvmName("partial3LastExt")
inline fun <A, B, C, D, R> F4<A, B, C, D, R>.partialLast3(b: B, c: C, d: D): F1<A, R> = partialLast3(this, b, c, d)
inline fun <A, B, C, D, R> partialLast3(crossinline f: F4<A, B, C, D, R>, b: B, c: C, d: D): F1<A, R> =
	{ a -> f(a, b, c, d) }

inline fun <A, B, C, D, R> partialLastS3(crossinline f: sF4<A, B, C, D, R>, b: B, c: C, d: D): sF1<A, R> =
	{ a -> f(a, b, c, d) }

@JvmName("curry0Ext")
inline fun <A, R> F1<A, R>.curry0(): (A) -> () -> R = curry0(this)
inline fun <A, R> curry0(crossinline f: F1<A, R>): (A) -> () -> R = { a -> partial(f, a) }

@JvmName("curryS0Ext")
inline fun <A, R> sF1<A, R>.curryS0(): suspend (A) -> suspend () -> R = curryS0(this)
inline fun <A, R> curryS0(crossinline f: sF1<A, R>): suspend (A) -> suspend () -> R =
	{ a -> partialS(f, a) }

@JvmName("curry0Ext")
inline fun <A, B, R> F2<A, B, R>.curry0(): (A) -> (B) -> () -> R = curry0(this)
inline fun <A, B, R> curry0(crossinline f: F2<A, B, R>): (A) -> (B) -> () -> R = { a -> { b -> partial2(f, a, b) } }

@JvmName("curryS0Ext")
inline fun <A, B, R> sF2<A, B, R>.curryS0(): suspend (A) -> suspend (B) -> suspend () -> R = curryS0(this)
inline fun <A, B, R> curryS0(crossinline f: sF2<A, B, R>): suspend (A) -> suspend (B) -> suspend () -> R =
	{ a -> { b -> partialS2(f, a, b) } }

@JvmName("curry0Ext")
inline fun <A, B, C, R> F3<A, B, C, R>.curry0(): (A) -> (B) -> (C) -> () -> R = curry0(this)
inline fun <A, B, C, R> curry0(crossinline f: F3<A, B, C, R>): (A) -> (B) -> (C) -> () -> R =
	{ a -> { b -> { c -> partial3(f, a, b, c) } } }

@JvmName("curryS0Ext")
inline fun <A, B, C, R> sF3<A, B, C, R>.curryS0(): suspend (A) -> suspend (B) -> suspend (C) -> suspend () -> R =
	curryS0(this)

inline fun <A, B, C, R> curryS0(crossinline f: sF3<A, B, C, R>): suspend (A) -> suspend (B) -> suspend (C) -> suspend () -> R =
	{ a -> { b -> { c -> partialS3(f, a, b, c) } } }


@JvmName("curryExt")
inline fun <A, B, R> F2<A, B, R>.curry(): (A) -> (B) -> R = curry(this)
inline fun <A, B, R> curry(crossinline f: F2<A, B, R>): (A) -> (B) -> R = { a -> partial(f, a) }

@JvmName("currySExt")
inline fun <A, B, R> sF2<A, B, R>.curryS(): suspend (A) -> suspend (B) -> R = curryS(this)
inline fun <A, B, R> curryS(crossinline f: sF2<A, B, R>): suspend (A) -> suspend (B) -> R = { a -> partialS(f, a) }

@JvmName("curryExt")
inline fun <A, B, C, R> F3<A, B, C, R>.curry(): (A) -> (B) -> (C) -> R = curry(this)
inline fun <A, B, C, R> curry(crossinline f: F3<A, B, C, R>): (A) -> (B) -> (C) -> R =
	{ a -> { b -> partial2(f, a, b) } }

@JvmName("currySExt")
inline fun <A, B, C, R> sF3<A, B, C, R>.curryS(): suspend (A) -> suspend (B) -> suspend (C) -> R = curryS(this)
inline fun <A, B, C, R> curryS(crossinline f: sF3<A, B, C, R>): suspend (A) -> suspend (B) -> suspend (C) -> R =
	{ a -> { b -> partialS2(f, a, b) } }

@JvmName("uncurryExt")
inline fun <A, B, R> ((A) -> (B) -> R).uncurry(): F2<A, B, R> = uncurry(this)
inline fun <A, B, R> uncurry(crossinline f: ((A) -> (B) -> R)): F2<A, B, R> = { a, b -> f(a).invoke(b) }

@JvmName("uncurrySExt")
inline fun <A, B, R> (suspend (A) -> suspend (B) -> R).uncurryS(): sF2<A, B, R> = uncurryS(this)
inline fun <A, B, R> uncurryS(crossinline f: (suspend (A) -> suspend (B) -> R)): sF2<A, B, R> =
	{ a, b -> f(a).invoke(b) }

@JvmName("uncurryExt")
inline fun <A, B, C, R> ((A) -> (B) -> (C) -> R).uncurry(): F3<A, B, C, R> = uncurry(this)
inline fun <A, B, C, R> uncurry(crossinline f: ((A) -> (B) -> (C) -> R)): F3<A, B, C, R> =
	{ a, b, c -> f(a).invoke(b).invoke(c) }

@JvmName("uncurrySExt")
inline fun <A, B, C, R> (suspend (A) -> suspend (B) -> suspend (C) -> R).uncurryS(): sF3<A, B, C, R> = uncurryS(this)
inline fun <A, B, C, R> uncurryS(crossinline f: (suspend (A) -> suspend (B) -> suspend (C) -> R)): sF3<A, B, C, R> =
	{ a, b, c -> f(a).invoke(b).invoke(c) }

@JvmName("uncurryExt")
inline fun <A, B, C, R> ((A, B) -> (C) -> R).uncurry(): F3<A, B, C, R> = uncurry(this)
inline fun <A, B, C, R> uncurry(crossinline f: ((A, B) -> (C) -> R)): F3<A, B, C, R> = { a, b, c -> f(a, b).invoke(c) }

@JvmName("uncurrySExt")
inline fun <A, B, C, R> (suspend (A, B) -> suspend (C) -> R).uncurryS(): sF3<A, B, C, R> = uncurryS(this)
inline fun <A, B, C, R> uncurryS(crossinline f: (suspend (A, B) -> suspend (C) -> R)): sF3<A, B, C, R> =
	{ a, b, c -> f(a, b).invoke(c) }

@JvmName("flipExt")
inline fun <A, B, R> F2<A, B, R>.flip(): F2<B, A, R> = flip(this)
inline fun <A, B, R> flip(crossinline f: F2<A, B, R>): F2<B, A, R> = { b, a -> f(a, b) }
inline fun <A, B, R> flipS(crossinline f: sF2<A, B, R>): sF2<B, A, R> = { b, a -> f(a, b) }

@JvmName("flipExt")
inline fun <A, B, R> ((A) -> (B) -> R).flip(): (B) -> (A) -> R = flip(this)
inline fun <A, B, R> flip(crossinline f: ((A) -> (B) -> R)): (B) -> (A) -> R = { b -> { a -> f(a).invoke(b) } }
inline fun <A, B, R> flipS(crossinline f: (suspend (A) -> suspend (B) -> R)): suspend (B) -> suspend (A) -> R =
	{ b -> { a -> f(a).invoke(b) } }

@JvmName("onExt")
inline infix fun <A, B, R> F2<B, B, R>.on(crossinline f: F1<A, B>): (A, A) -> R = on(this, f)
inline fun <A, B, R> on(crossinline f1: F2<B, B, R>, crossinline f2: F1<A, B>): (A, A) -> R =
	{ a1, a2 -> f1(f2(a1), f2(a2)) }

@JvmName("onExt")
inline infix fun <A, B, R> ((B) -> (B) -> R).on(crossinline f: F1<A, B>) = on(this, f)
inline fun <A, B, R> on(crossinline f1: ((B) -> (B) -> R), crossinline f2: F1<A, B>): (A) -> (A) -> R =
	{ a1: A -> val fa1: B = f2(a1); { a2: A -> f1(fa1)(f2(a2)) } }


/**
 * Experimental functions
 */
@RequiresOptIn(level = RequiresOptIn.Level.WARNING)
@Target(
	AnnotationTarget.CLASS,
	AnnotationTarget.ANNOTATION_CLASS,
	AnnotationTarget.PROPERTY,
	AnnotationTarget.FIELD,
	AnnotationTarget.LOCAL_VARIABLE,
	AnnotationTarget.VALUE_PARAMETER,
	AnnotationTarget.CONSTRUCTOR,
	AnnotationTarget.FUNCTION,
	AnnotationTarget.PROPERTY_GETTER,
	AnnotationTarget.PROPERTY_SETTER,
	AnnotationTarget.TYPEALIAS
)
@Retention(AnnotationRetention.BINARY)
annotation class ExperimentalFunctions

/**
 * Allows curried operations without converting a function using curry().
 *
 * Note: Must be explicitly imported, the IDE does not suggest importing these.
 */

// for any (f: (A, B) -> R), f(a) becomes (B) -> R, which is equivalent to partial application
@ExperimentalFunctions
inline operator fun <A, B, R> F2<A, B, R>.invoke(a: A): F1<B, R> = partial(this, a)

// for any (f: (A, B, C) -> R), f(a) becomes (B, C) -> R, which is equivalent to partial application
@ExperimentalFunctions
inline operator fun <A, B, C, R> F3<A, B, C, R>.invoke(a: A): F2<B, C, R> = partial(this, a)

// for any (f: (A, B, C, D) -> R), f(a) becomes (B, C, D) -> R, which is equivalent to partial application
@ExperimentalFunctions
operator fun <A, B, C, D, R> F4<A, B, C, D, R>.invoke(a: A): F3<B, C, D, R> = partial(this, a)
