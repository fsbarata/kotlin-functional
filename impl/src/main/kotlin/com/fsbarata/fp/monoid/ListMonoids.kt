package com.fsbarata.fp.monoid

import com.fsbarata.fp.concepts.Monoid
import com.fsbarata.fp.concepts.Semigroup
import com.fsbarata.fp.types.Option

fun <A> optionMonoid(semigroup: Semigroup<A>) = object : Monoid<Option<A>> {
	override fun empty() = Option.empty<A>()
	override fun Option<A>.combine(other: Option<A>) =
			with(semigroup) {
				other.map { value?.combine(it) ?: it } orOption this@combine
			}
}

fun <A> listConcatMonoid() = object : Monoid<List<A>> {
	override fun empty() = emptyList<A>()
	override fun List<A>.combine(other: List<A>) = this + other
}

