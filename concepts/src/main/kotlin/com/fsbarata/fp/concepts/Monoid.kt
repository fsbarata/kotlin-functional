package com.fsbarata.fp.concepts

interface Monoid<A : Monoid<A>> : Semigroup<A> {
	fun empty(): A
}

fun <M : Monoid<M>> List<M>.foldLeft(): M =
		fold(first().empty()) { a, b -> a.combine(b) }

fun <M : Monoid<M>> List<M>.foldRight(): M =
		foldRight(first().empty()) { a, b -> a.combine(b) }

fun <M : Monoid<M>> Foldable<M>.fold(empty: M): M =
		fold(empty) { a, b -> a.combine(b) }


