package com.fsbarata.fp.concepts

interface Monoid<A>: Semigroup<A> {
	fun empty(): Monoid<A>

	override fun append(a: Semigroup<A>): Monoid<A>
}

fun <A> List<Monoid<A>>.foldLeft(): Monoid<A> =
		concat(first().empty()) as Monoid<A>

fun <A> List<Monoid<A>>.foldRight(): Monoid<A> =
		foldRight(first().empty()) { a, b -> a.append(b) }
