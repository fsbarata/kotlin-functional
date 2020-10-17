package com.fsbarata.fp.concepts

interface Monoid<A>: Semigroup<A> {
	val empty: A
}

fun <A> monoid(empty: A, combine: (A, A) -> A) = object: Monoid<A> {
	override val empty = empty
	override fun A.combine(other: A) = combine(this, other)
}

fun <A> Semigroup<A>.monoid(empty: A): Monoid<A> = object: Monoid<A>, Semigroup<A> by this {
	override val empty = empty
}

fun <A> Foldable<A>.fold(monoid: Monoid<A>) =
	with(monoid) { fold(empty, this) }

fun <A> List<A>.fold(monoid: Monoid<A>) =
	with(monoid) { fold(empty, this) }

fun <A> Sequence<A>.fold(monoid: Monoid<A>) =
	with(monoid) { fold(empty, this) }

fun <A> List<A>.foldRight(monoid: Monoid<A>) =
	with(monoid) { fold(empty, this) }
