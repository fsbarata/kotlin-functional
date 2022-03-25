@file:Suppress("NOTHING_TO_INLINE")

package com.github.fsbarata.functional.control.arrow

import com.github.fsbarata.functional.data.F1
import com.github.fsbarata.functional.data.either.Either
import com.github.fsbarata.functional.data.tuple.Tuple2
import kotlin.jvm.JvmName


@JvmName("firstExt")
inline fun <A, B, R> F1<A, R>.first(): F1<Pair<A, B>, Pair<R, B>> = first(this)
inline fun <A, B, R> first(crossinline f: F1<A, R>): F1<Pair<A, B>, Pair<R, B>> = { Pair(f(it.first), it.second) }

@JvmName("firstTExt")
inline fun <A, B, R> F1<A, R>.firstT(): F1<Tuple2<A, B>, Tuple2<R, B>> = firstT(this)
inline fun <A, B, R> firstT(crossinline f: F1<A, R>): F1<Tuple2<A, B>, Tuple2<R, B>> = { Tuple2(f(it.x), it.y) }

@JvmName("secondExt")
inline fun <A, B, R> F1<B, R>.second(): F1<Pair<A, B>, Pair<A, R>> = second(this)
inline fun <A, B, R> second(crossinline f: F1<B, R>): F1<Pair<A, B>, Pair<A, R>> = { Pair(it.first, f(it.second)) }

@JvmName("secondTExt")
inline fun <A, B, R> F1<B, R>.secondT(): F1<Tuple2<A, B>, Tuple2<A, R>> = secondT(this)
inline fun <A, B, R> secondT(crossinline f: F1<B, R>): F1<Tuple2<A, B>, Tuple2<A, R>> = { Tuple2(it.x, f(it.y)) }

@JvmName("first3Ext")
inline fun <A, B, C, R> F1<A, R>.first3(): F1<Triple<A, B, C>, Triple<R, B, C>> = first3(this)
inline fun <A, B, C, R> first3(crossinline f: F1<A, R>): F1<Triple<A, B, C>, Triple<R, B, C>> =
	{ Triple(f(it.first), it.second, it.third) }

@JvmName("second3Ext")
inline fun <A, B, C, R> F1<B, R>.second3(): F1<Triple<A, B, C>, Triple<A, R, C>> = second3(this)
inline fun <A, B, C, R> second3(crossinline f: F1<B, R>): F1<Triple<A, B, C>, Triple<A, R, C>> =
	{ Triple(it.first, f(it.second), it.third) }

@JvmName("third3Ext")
inline fun <A, B, C, R> F1<C, R>.third3(): F1<Triple<A, B, C>, Triple<A, B, R>> = third3(this)
inline fun <A, B, C, R> third3(crossinline f: F1<C, R>): F1<Triple<A, B, C>, Triple<A, B, R>> =
	{ Triple(it.first, it.second, f(it.third)) }

@JvmName("splitExt")
inline infix fun <A, R, B, RR> F1<A, R>.split(crossinline other: F1<B, RR>) = split(this, other)
inline fun <A, R, B, RR> split(crossinline f1: F1<A, R>, crossinline f2: F1<B, RR>): F1<Pair<A, B>, Pair<R, RR>> =
	{ Pair(f1(it.first), f2(it.second)) }

@JvmName("splitTExt")
inline infix fun <A, R, B, RR> F1<A, R>.splitT(crossinline other: F1<B, RR>) = splitT(this, other)
inline fun <A, R, B, RR> splitT(crossinline f1: F1<A, R>, crossinline f2: F1<B, RR>): F1<Tuple2<A, B>, Tuple2<R, RR>> =
	{ Tuple2(f1(it.x), f2(it.y)) }

@JvmName("fanoutExt")
inline infix fun <A, R, RR> F1<A, R>.fanout(crossinline other: F1<A, RR>) = fanout(this, other)
inline fun <A, R, RR> fanout(crossinline f1: F1<A, R>, crossinline f2: F1<A, RR>): F1<A, Pair<R, RR>> =
	{ Pair(f1(it), f2(it)) }

@JvmName("fanoutTExt")
inline infix fun <A, R, RR> F1<A, R>.fanoutT(crossinline other: F1<A, RR>) = fanoutT(this, other)
inline fun <A, R, RR> fanoutT(crossinline f1: F1<A, R>, crossinline f2: F1<A, RR>): F1<A, Tuple2<R, RR>> =
	{ Tuple2(f1(it), f2(it)) }

@JvmName("leftExt")
inline fun <A, R, PASS> F1<A, R>.left(): F1<Either<A, PASS>, Either<R, PASS>> = left(this)
inline fun <A, R, PASS> left(crossinline f: F1<A, R>): F1<Either<A, PASS>, Either<R, PASS>> =
	{ it.mapLeft(f) }

@JvmName("rightExt")
inline fun <A, R, PASS> F1<A, R>.right(): F1<Either<PASS, A>, Either<PASS, R>> = right(this)
inline fun <A, R, PASS> right(crossinline f: F1<A, R>): F1<Either<PASS, A>, Either<PASS, R>> =
	{ it.map(f) }

@JvmName("splitChoiceExt")
inline infix fun <A, L, B, R> F1<A, L>.splitChoice(crossinline other: F1<B, R>) = splitChoice(this, other)
inline fun <A, L, B, R> splitChoice(
	crossinline f1: F1<A, L>,
	crossinline f2: F1<B, R>,
): F1<Either<A, B>, Either<L, R>> = { it.bimap(f1, f2) }

@JvmName("faninExt")
inline infix fun <A, B, R> F1<A, R>.fanin(crossinline other: F1<B, R>) = fanin(this, other)
inline fun <A, B, R> fanin(crossinline f1: F1<A, R>, crossinline f2: F1<B, R>): F1<Either<A, B>, R> =
	{ it.fold(f1, f2) }

@JvmName("fappExt")
inline fun <A, R> Pair<F1<A, R>, A>.fapp() = fapp(this)
inline fun <A, R> fapp(f: F1<A, R>, a: A): R = f(a)
inline fun <A, R> fapp(p: Pair<F1<A, R>, A>): R = p.first.invoke(p.second)

