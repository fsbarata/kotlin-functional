package com.github.fsbarata.functional.data

interface Monoid<A>: Semigroup.Scope<A> {
	val empty: A
}

fun <A: Semigroup<A>> monoid(empty: A) = semigroupScopeOf<A>().monoid(empty)

fun <A> Semigroup.Scope<A>.monoid(empty: A): Monoid<A> =
	object: Monoid<A>, Semigroup.Scope<A> by this@monoid {
		override val empty: A = empty
	}

inline fun <A> monoidOf(empty: A, semigroup: Semigroup.Scope<A>) = semigroup.monoid(empty)

