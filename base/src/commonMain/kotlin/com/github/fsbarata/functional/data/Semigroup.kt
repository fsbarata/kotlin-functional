package com.github.fsbarata.functional.data

import com.github.fsbarata.functional.data.list.NonEmptyList

/**
 * A semigroup is a scope where a function that can combine values of a single type exists and
 * respects the associativity property.
 *
 * For any type that defines an associative plus method, there exists a Semigroup where combine is plus
 * For eg., Numbers, String
 */
interface Semigroup<A> {
	fun concatWith(other: A): A

	fun interface Scope<A> {
		fun concat(a1: A, a2: A): A

		fun A.concatWith(a2: A): A = concat(this, a2)
	}
}

inline fun <A: Semigroup<A>> semigroupScopeOf(): Semigroup.Scope<A> = Semigroup.Scope(::concat)

inline fun <A: Semigroup<A>> concat(a1: A, a2: A): A = a1.concatWith(a2)

fun <A: Semigroup<A>> A.stimes(n: Int): A {
	return semigroupScopeOf<A>().stimes(this, n)
}

fun <A> Semigroup.Scope<A>.stimes(a: A, n: Int): A {
	require(n >= 1)
	return add(a, a, n - 1)
}

private tailrec fun <A> Semigroup.Scope<A>.add(ac: A, a: A, n: Int): A =
	if (n == 0) ac
	else add(concat(ac, a), a, n - 1)

fun <A: Semigroup<A>> NonEmptyList<A>.sconcat() =
	sconcat(::concat)

fun <A> NonEmptyList<A>.sconcat(semigroupScope: Semigroup.Scope<A>) =
	reduce { a1, a2 -> semigroupScope.concat(a1, a2) }
