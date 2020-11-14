package com.github.fsbarata.functional.data

/**
 * A semigroup is a scope where a function that can combine values of a single type exists and
 * respects the associativity property.
 *
 * For eg., for any type that defines a plus method, there exists a Semigroup where combine is plus
 */
interface Semigroup<A> {
	fun combineWith(other: A): A
}

fun <A: Semigroup<A>> A.times(n: Int): (A) -> A {
	require(n >= 1)
	return { a: A -> add(a, n - 1) }
}

private tailrec fun <A: Semigroup<A>> A.add(a: A, n: Int): A =
	if (n == 0) this
	else combineWith(a).add(a, n - 1)

class Dual<A: Semigroup<A>>(val get: A): Semigroup<Dual<A>> {
	override fun combineWith(other: Dual<A>) = Dual(other.get.combineWith(get))
}
