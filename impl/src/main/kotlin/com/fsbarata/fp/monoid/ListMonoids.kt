package com.fsbarata.fp.monoid

import com.fsbarata.fp.concepts.Monoid
import com.fsbarata.fp.types.Option

fun <A> Monoid<A>.option() = object : Monoid<Option<A>> {
	override fun empty() = Option.empty<A>()
	override fun Option<A>.combine(other: Option<A>) =
			other.map { value?.combine(it) ?: it } orOption this@combine
}

fun <A> listConcatMonoid() = object : Monoid<List<A>> {
	override fun empty() = emptyList<A>()
	override fun List<A>.combine(other: List<A>) = this + other
}

