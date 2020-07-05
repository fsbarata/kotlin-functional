package com.fsbarata.fp.concepts

interface Semigroup<A> {
	fun append(a: Semigroup<A>): Semigroup<A>

	operator fun plus(a: Semigroup<A>) = append(a)

	operator fun times(n: Int): Semigroup<A> {
		require(n >= 1)
		return addMult(this, n)
	}
}

private tailrec fun <A> Semigroup<A>.addMult(a: Semigroup<A>, n: Int): Semigroup<A> =
		if (n == 1) this
		else append(a).addMult(a, n - 1)

fun <A> List<Semigroup<A>>.concat(initialValue: Semigroup<A>) =
		fold(initialValue) { a, b -> a.append(b) }
