package com.fsbarata.fp.concepts

interface Monoid<A>: Semigroup<A> {
	val empty: A
}

fun <A> monoid(empty: A, sg: Semigroup<A>): Monoid<A> = sg.monoid(empty)

fun <A> Semigroup<A>.monoid(empty: A): Monoid<A> = object: Monoid<A>, Semigroup<A> by this {
	override val empty = empty
}

fun <A> Foldable<A>.fold(monoid: Monoid<A>) = fold(monoid.empty, monoid::combine)

fun <A> List<A>.fold(monoid: Monoid<A>) = fold(monoid.empty, monoid::combine)

fun <A> Sequence<A>.fold(monoid: Monoid<A>) = fold(monoid.empty, monoid::combine)

fun <A> List<A>.foldRight(monoid: Monoid<A>) = foldRight(monoid.empty, monoid::combine)
