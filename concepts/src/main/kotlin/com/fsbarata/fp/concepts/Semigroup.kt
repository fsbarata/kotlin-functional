package com.fsbarata.fp.concepts

interface Semigroup<A> {
	fun A.combine(a: A): A

	operator fun A.plus(a: A) = combine(a)

	operator fun A.times(n: Int): A {
		require(n >= 1)
		return add(this, n)
	}

	private tailrec fun A.add(a: A, n: Int): A =
			if (n == 1) this
			else this.combine(a).add(a, n - 1)


	fun List<A>.fold(initialValue: A) =
			fold(initialValue) { a, b -> a.combine(b) }

	fun List<A>.foldRight(initialValue: A) =
			foldRight(initialValue) { a, b -> a.combine(b) }

	fun Foldable<A>.fold(initialValue: A) =
			fold(initialValue) { a, b -> a.combine(b) }
}

