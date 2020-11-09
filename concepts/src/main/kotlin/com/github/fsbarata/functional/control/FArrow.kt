package com.github.fsbarata.functional.control

import com.github.fsbarata.functional.data.*

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

