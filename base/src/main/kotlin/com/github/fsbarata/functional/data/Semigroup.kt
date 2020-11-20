package com.github.fsbarata.functional.data

import com.github.fsbarata.functional.data.list.NonEmptyList

/**
 * A semigroup is a scope where a function that can combine values of a single type exists and
 * respects the associativity property.
 *
 * For eg., for any type that defines a plus method, there exists a Semigroup where combine is plus
 */
interface Semigroup<A> {
	fun combineWith(other: A): A
}

fun <A: Semigroup<A>> combine(a1: A, a2: A) = a1.combineWith(a2)

fun <A: Semigroup<A>> A.stimes(n: Int): A {
	require(n >= 1)
	return add(this, n - 1)
}

private tailrec fun <A: Semigroup<A>> A.add(a: A, n: Int): A =
	if (n == 0) this
	else combineWith(a).add(a, n - 1)

class Dual<A: Semigroup<A>>(val get: A): Semigroup<Dual<A>> {
	override fun combineWith(other: Dual<A>) = Dual(other.get.combineWith(get))
}

fun <A: Semigroup<A>> A.dual() = Dual(this)

fun <A: Semigroup<A>> NonEmptyList<A>.sconcat() =
	reduce { a1, a2 -> a1.combineWith(a2) }
