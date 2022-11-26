@file:Suppress("NOTHING_TO_INLINE")

package com.github.fsbarata.functional.data.tuple

import com.github.fsbarata.functional.BiContext
import com.github.fsbarata.functional.Context
import com.github.fsbarata.functional.control.Applicative
import com.github.fsbarata.functional.control.Comonad
import com.github.fsbarata.functional.data.*
import com.github.fsbarata.io.Serializable
import kotlin.jvm.JvmName

@Suppress("OVERRIDE_BY_INLINE")
data class Tuple2<X, Y>(val x: X, val y: Y):
	Traversable<Tuple2Context<X>, Y>,
	BiFunctor<Tuple2BiContext, X, Y>,
	Comonad<Tuple2Context<X>, Y>,
	Serializable {
	override val scope get() = Scope<X>()

	override fun extract() = y

	override inline fun <B> map(f: (Y) -> B) =
		Tuple2(x, f(y))

	override fun <C> mapLeft(f: (X) -> C) =
		Tuple2(f(x), y)

	override fun <C, D> bimap(f: (X) -> C, g: (Y) -> D) =
		Tuple2(f(x), g(y))

	override inline fun <F, B> traverse(
		appScope: Applicative.Scope<F>,
		f: (Y) -> Context<F, B>,
	): Context<F, Tuple2<X, B>> = appScope.map(f(y)) { Tuple2(x, it) }

	override inline fun <R> foldL(initialValue: R, accumulator: (R, Y) -> R): R =
		accumulator(initialValue, y)

	override inline fun <R> foldR(initialValue: R, accumulator: (Y, R) -> R): R =
		accumulator(y, initialValue)

	override inline fun <M> foldMap(monoid: Monoid<M>, f: (Y) -> M): M = f(y)

	@Suppress("OVERRIDE_BY_INLINE")
	override inline fun <B> extend(f: (Comonad<Tuple2Context<X>, Y>) -> B): Tuple2<X, B> =
		coflatMap(f)

	inline fun <B> coflatMap(f: (Tuple2<X, Y>) -> B): Tuple2<X, B> =
		Tuple2(x, f(this))

	override fun duplicate() = Tuple2(x, this)

	fun swap() = Tuple2(y, x)

	override fun toString(): String = "($x, $y)"

	class Scope<X>: Traversable.Scope<Tuple2Context<X>>
}

internal typealias Tuple2Context<X> = Tuple2<X, *>
internal typealias Tuple2BiContext = Tuple2<*, *>

val <X, Y> Context<Tuple2Context<X>, Y>.asTuple
	get() = this as Tuple2<X, Y>

val <X, Y> BiContext<Tuple2BiContext, X, Y>.asTuple
	get() = this as Tuple2<X, Y>

inline fun <A, B> Pair<A, B>.f() = toTuple()
inline fun <A, B, X, Y> Pair<A, B>.f(block: Tuple2<A, B>.() -> Context<Tuple2Context<X>, Y>): Tuple2<X, Y> =
	f().block().asTuple

inline fun <X, Y> Pair<X, Y>.toTuple() = Tuple2(first, second)
inline fun <X, Y> Tuple2<X, Y>.toPair() = Pair(x, y)

inline fun <A, B, R> F2<A, B, R>.packT(): (Tuple2<A, B>) -> R = { t: Tuple2<A, B> -> invoke(t.x, t.y) }
inline fun <A, B, R> F1<Tuple2<A, B>, R>.unpackT(): F2<A, B, R> = { a: A, b: B -> invoke(Tuple2(a, b)) }

@JvmName("tuplefExt")
inline fun <A, B, R> F1<Pair<A, B>, R>.tuplef(): F1<Tuple2<A, B>, R> = tuplef(this)
inline fun <A, B, R> tuplef(crossinline f: F1<Pair<A, B>, R>): F1<Tuple2<A, B>, R> = compose(f, Tuple2<A, B>::toPair)

@JvmName("pairfExt")
inline fun <A, B, R> F1<Tuple2<A, B>, R>.pairf(): F1<Pair<A, B>, R> = pairf(this)
inline fun <A, B, R> pairf(crossinline f: F1<Tuple2<A, B>, R>): F1<Pair<A, B>, R> = { f(Tuple2(it.first, it.second)) }

@JvmName("entryfExt")
inline fun <K, V, R> F1<Pair<K, V>, R>.entryf(): F1<Map.Entry<K, V>, R> = entryf(this)
inline fun <K, V, R> entryf(crossinline f: F1<Pair<K, V>, R>): F1<Map.Entry<K, V>, R> = { f(it.toPair()) }

@JvmName("entryfTExt")
inline fun <K, V, R> F1<Tuple2<K, V>, R>.entryf(): F1<Map.Entry<K, V>, R> = entryf(this)
@JvmName("entryfT")
inline fun <K, V, R> entryf(crossinline f: F1<Tuple2<K, V>, R>): F1<Map.Entry<K, V>, R> = { f(it.toPair().toTuple()) }
