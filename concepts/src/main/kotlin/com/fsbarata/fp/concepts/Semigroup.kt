package com.fsbarata.fp.concepts

interface Semigroup<A: Semigroup<A>> {
	fun append(a: A): A

	operator fun plus(a: A) = append(a)

	operator fun times(n: Int): A {
		require(n >= 1)
		val a = this as A
		return a.addMult(a, n)
	}
}

private tailrec fun <A: Semigroup<A>> A.addMult(a: A, n: Int): A =
		if (n == 1) this
		else append(a).addMult(a, n - 1)

fun <A: Semigroup<A>> List<A>.concat(initialValue: Semigroup<A>) =
		fold(initialValue) { a, b -> a.append(b) }
