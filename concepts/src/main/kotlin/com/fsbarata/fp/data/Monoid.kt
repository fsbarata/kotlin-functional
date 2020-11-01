package com.fsbarata.fp.data

interface Monoid<A>: Semigroup<A> {
	val empty: A
}

fun <A> monoid(empty: A, sg: Semigroup<A>): Monoid<A> = sg.monoid(empty)

fun <A> Semigroup<A>.monoid(empty: A): Monoid<A> = object: Monoid<A>, Semigroup<A> by this {
	override val empty = empty
}

fun <A> List<A>.foldL(monoid: Monoid<A>) = fold(monoid.empty, monoid::combine)

fun <A> Sequence<A>.foldL(monoid: Monoid<A>) = fold(monoid.empty, monoid::combine)

fun <A> List<A>.foldR(monoid: Monoid<A>) = foldRight(monoid.empty, monoid::combine)


fun <A> Monoid<A>.dual() =
	(this as Semigroup<A>).dual().monoid(empty)

typealias Endo<A> = (A) -> A

fun <A> endoMonoid(): Monoid<Endo<A>> = monoid(id(), Endo<A>::composeForward)

