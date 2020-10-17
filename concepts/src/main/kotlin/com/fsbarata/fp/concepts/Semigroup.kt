package com.fsbarata.fp.concepts

/**
 * A semigroup is a scope where a function that can combine values of a single type exists and
 * respects the associativity property.
 *
 * For eg., for any type that defines a plus method, there exists a Semigroup where combine is plus
 */
fun interface Semigroup<A> {
	fun A.combine(other: A): A
}

fun <A> Semigroup<A>.times(n: Int): (A) -> A {
	require(n >= 1)
	return { a: A -> add(a, a, n) }
}

private tailrec fun <A> Semigroup<A>.add(a1: A, a2: A, n: Int): A =
	if (n == 1) a1
	else add(a1.combine(a2), a2, n - 1)


fun <A> semigroup(combine: (A, A) -> A) = object: Semigroup<A> {
	override fun A.combine(other: A) = combine(this, other)
}

fun <A> Foldable<A>.fold(initialValue: A, semigroup: Semigroup<A>) =
	with(semigroup) { fold(initialValue) { acc, a -> acc.combine(a) } }

fun <A> List<A>.fold(initialValue: A, semigroup: Semigroup<A>) =
	with(semigroup) { fold(initialValue) { acc, a -> acc.combine(a) } }

fun <A> Sequence<A>.fold(initialValue: A, semigroup: Semigroup<A>) =
	with(semigroup) { fold(initialValue) { acc, a -> acc.combine(a) } }

fun <A> List<A>.scan(initialValue: A, semigroup: Semigroup<A>) =
	with(semigroup) { scan(initialValue) { acc, a -> acc.combine(a) } }

fun <A> Sequence<A>.scan(initialValue: A, semigroup: Semigroup<A>) =
	with(semigroup) { scan(initialValue) { acc, a -> acc.combine(a) } }

fun <A> List<A>.foldRight(initialValue: A, semigroup: Semigroup<A>) =
	with(semigroup) { foldRight(initialValue) { acc, a -> acc.combine(a) } }
