package com.fsbarata.fp.concepts

interface Monoid<A> : Semigroup<A> {
	fun empty(): A
}

fun <A> Foldable<A>.fold(monoid: Monoid<A>) =
		with(monoid) { fold(empty(), this) }
fun <A> List<A>.fold(monoid: Monoid<A>) =
		with(monoid) { fold(empty(), this) }
fun <A> List<A>.foldRight(monoid: Monoid<A>) =
		with(monoid) { fold(empty(), this) }
