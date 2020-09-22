package com.fsbarata.fp.concepts

interface Semigroup<A> {
	fun A.combine(other: A): A

	fun A.times(n: Int): A {
		require(n >= 1)
		return add(this, n)
	}

	private tailrec fun A.add(a: A, n: Int): A =
			if (n == 1) this
			else combine(a).add(a, n - 1)
}

fun <A> semigroup(combine: (A, A) -> A) = object : Semigroup<A> {
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
