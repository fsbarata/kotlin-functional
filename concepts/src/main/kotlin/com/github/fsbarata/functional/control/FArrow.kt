package com.github.fsbarata.functional.control

import com.github.fsbarata.functional.data.F1
import com.github.fsbarata.functional.data.composeForward

interface FArrow<B, C>: F1<B, C>, Arrow<FArrow<*, *>, B, C> {
	override val scope get() = Companion

	override fun <D> compose(other: Category<FArrow<*, *>, C, D>): Arrow<FArrow<*, *>, B, D> =
		Arrow((this as F1<B, C>).composeForward(other.asFArrow()))

	override fun <D> first(): Arrow<FArrow<*, *>, Pair<B, D>, Pair<C, D>> =
		Arrow((this as F1<B, C>).first())

	override fun <D> second(): Arrow<FArrow<*, *>, Pair<D, B>, Pair<D, C>> =
		Arrow((this as F1<B, C>).second())

	override infix fun <D, E> split(other: Arrow<FArrow<*, *>, D, E>): FArrow<Pair<B, D>, Pair<C, E>> =
		Arrow((this as F1<B, C>) split (other.asFArrow() as F1<D, E>))

	override infix fun <D> fanout(other: Arrow<FArrow<*, *>, B, D>): FArrow<B, Pair<C, D>> =
		Arrow((this as F1<B, C>) fanout (other.asFArrow()))

	companion object: Arrow.Scope<FArrow<*, *>> {
		override fun <A, B> arr(f: (A) -> B) = Arrow(f)
	}
}

fun <B, C> Arrow(f: (B) -> C): FArrow<B, C> = object: FArrow<B, C>, (B) -> C by f {}

fun <B, C> Category<FArrow<*, *>, B, C>.asFArrow() =
	this as FArrow<B, C>


fun <B, C, D> F1<B, C>.first(): F1<Pair<B, D>, Pair<C, D>> =
	{ Pair(invoke(it.first), it.second) }

fun <B, C, D> F1<B, C>.second(): F1<Pair<D, B>, Pair<D, C>> =
	{ Pair(it.first, invoke(it.second)) }

inline infix fun <B, C, D, E> F1<B, C>.split(crossinline other: F1<D, E>): F1<Pair<B, D>, Pair<C, E>> =
	{ Pair(this(it.first), other(it.second)) }

inline infix fun <B, C, D> F1<B, C>.fanout(crossinline other: F1<B, D>): F1<B, Pair<C, D>> =
	{ Pair(this(it), other(it)) }
