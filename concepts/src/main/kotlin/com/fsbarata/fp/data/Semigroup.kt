package com.fsbarata.fp.data

/**
 * A semigroup is a scope where a function that can combine values of a single type exists and
 * respects the associativity property.
 *
 * For eg., for any type that defines a plus method, there exists a Semigroup where combine is plus
 */
fun interface Semigroup<A> {
	fun combine(a1: A, a2: A): A
}

fun <A> Semigroup<A>.times(n: Int): (A) -> A {
	require(n >= 1)
	return { a: A -> add(a, a, n) }
}

private tailrec fun <A> Semigroup<A>.add(a1: A, a2: A, n: Int): A =
	if (n == 1) a1
	else add(combine(a1, a2), a2, n - 1)

fun <A> Semigroup<A>.dual(): Semigroup<A> =
	Semigroup { a1, a2 -> combine(a2, a1) }