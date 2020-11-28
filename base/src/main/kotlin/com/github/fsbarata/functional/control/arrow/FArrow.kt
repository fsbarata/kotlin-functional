@file:Suppress("NOTHING_TO_INLINE")
package com.github.fsbarata.functional.control.arrow

import com.github.fsbarata.functional.data.F1
import com.github.fsbarata.functional.data.either.Either


inline fun <B, C, D> F1<B, C>.first(): F1<Pair<B, D>, Pair<C, D>> =
	{ Pair(invoke(it.first), it.second) }
inline fun <B, C, D> F1<B, C>.second(): F1<Pair<D, B>, Pair<D, C>> =
	{ Pair(it.first, invoke(it.second)) }

inline infix fun <A, R, B, RR> F1<A, R>.split(crossinline other: F1<B, RR>): F1<Pair<A, B>, Pair<R, RR>> =
	{ Pair(this(it.first), other(it.second)) }
inline infix fun <A, R, RR> F1<A, R>.fanout(crossinline other: F1<A, RR>): F1<A, Pair<R, RR>> =
	{ Pair(this(it), other(it)) }

inline fun <A, R, PASS> F1<A, R>.left(): F1<Either<A, PASS>, Either<R, PASS>> =
	{ it.mapLeft(this) }
inline fun <A, R, PASS> F1<A, R>.right(): F1<Either<PASS, A>, Either<PASS, R>> =
	{ it.map(this) }
inline infix fun <A, R, B, RR> F1<A, R>.splitChoice(crossinline other: F1<B, RR>): F1<Either<A, B>, Either<R, RR>> =
	{ it.bimap(this, other) }
inline infix fun <A, B, R> F1<A, R>.fanin(crossinline other: F1<B, R>): F1<Either<A, B>, R> =
	{ it.fold(this, other) }

inline fun <A, R> fapp(): F1<Pair<F1<A, R>, A>, R> = { (f, a) -> f(a) }
