package com.fsbarata.fp.monoid

import com.fsbarata.fp.concepts.Monoid
import com.fsbarata.fp.types.Optional

fun <A> Monoid<A>.option() = object : Monoid<Optional<A>> {
	override fun empty() = Optional.empty<A>()
	override fun Optional<A>.combine(other: Optional<A>) =
			other.map { value?.combine(it) ?: it } orOptional this@combine
}

fun <A> listConcatMonoid() = object : Monoid<List<A>> {
	override fun empty() = emptyList<A>()
	override fun List<A>.combine(other: List<A>) = this + other
}

