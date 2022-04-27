@file:Suppress("NOTHING_TO_INLINE")

package com.github.fsbarata.functional.data.tuple

import com.github.fsbarata.functional.data.F1
import com.github.fsbarata.functional.data.F2
import com.github.fsbarata.functional.data.F3
import kotlin.jvm.JvmName

inline fun <A, B, R> Pair<A, B>.mapLeft(f: (A) -> R): Pair<R, B> = Pair(f(first), second)
inline fun <A, B, R> Pair<A, B>.map(f: (B) -> R): Pair<A, R> = Pair(first, f(second))
inline fun <A, B, C, D> Pair<A, B>.bimap(f: (A) -> C, g: (B) -> D): Pair<C, D> = Pair(f(first), g(second))
inline fun <A, R> Pair<A, A>.both(f: (A) -> R): Pair<R, R> = bimap(f, f)

inline fun <A, B, R> Pair<A, B>.coflatMap(f: (Pair<A, B>) -> R): Pair<A, R> = Pair(first, f(this))
inline fun <A, B> Pair<A, B>.duplicate(): Pair<A, Pair<A, B>> = Pair(first, this)

fun <A, B> Pair<A, B>.swap(): Pair<B, A> = Pair(second, first)

@JvmName("packExt")
inline fun <A, B, R> F2<A, B, R>.pack(): (Pair<A, B>) -> R = pack(this)
inline fun <A, B, R> pack(crossinline f: F2<A, B, R>): (Pair<A, B>) -> R = { t: Pair<A, B> -> f(t.first, t.second) }

@JvmName("unpackExt")
inline fun <A, B, R> F1<Pair<A, B>, R>.unpack(): F2<A, B, R> = unpack(this)
inline fun <A, B, R> unpack(crossinline f: F1<Pair<A, B>, R>): F2<A, B, R> = { a: A, b: B -> f(a to b) }

@JvmName("packExt")
inline fun <A, B, C, R> F3<A, B, C, R>.pack(): (Triple<A, B, C>) -> R = pack(this)
inline fun <A, B, C, R> pack(crossinline f: F3<A, B, C, R>): (Triple<A, B, C>) -> R =
	{ t: Triple<A, B, C> -> f(t.first, t.second, t.third) }

@JvmName("unpackExt")
inline fun <A, B, C, R> F1<Triple<A, B, C>, R>.unpack(): F3<A, B, C, R> = unpack(this)
inline fun <A, B, C, R> unpack(crossinline f: F1<Triple<A, B, C>, R>): F3<A, B, C, R> =
	{ a: A, b: B, c: C -> f(Triple(a, b, c)) }

fun <A> dupe(a: A) = Pair(a, a)
