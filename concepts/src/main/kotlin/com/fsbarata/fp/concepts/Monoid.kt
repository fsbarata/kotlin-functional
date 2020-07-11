package com.fsbarata.fp.concepts

interface Monoid<A> : Semigroup<A> {
	fun empty(): A

	fun List<A>.foldLeft() = fold(empty())

	fun List<A>.foldRight() = foldRight(empty())

	fun Foldable<A>.fold() = fold(empty())
}

