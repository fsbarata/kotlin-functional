package com.github.fsbarata.functional.data

interface Monoid<A> {
	val empty: A
	fun combine(a1: A, a2: A): A
}

fun <A> monoid(empty: A, combine: (A, A) -> A) = object: Monoid<A> {
	override val empty: A = empty
	override fun combine(a1: A, a2: A) = combine(a1, a2)
}

fun <A: Semigroup<A>> monoid(empty: A) = object: Monoid<A> {
	override val empty: A = empty
	override fun combine(a1: A, a2: A) = a1.combineWith(a2)
}

fun <A: Semigroup<A>> List<A>.foldL(monoid: Monoid<A>) = fold(monoid.empty) { r, a -> r.combineWith(a) }

fun <A: Semigroup<A>> Sequence<A>.foldL(monoid: Monoid<A>) = fold(monoid.empty) { r, a -> r.combineWith(a) }

fun <A: Semigroup<A>> List<A>.foldR(monoid: Monoid<A>) = foldRight(monoid.empty) { r, a -> r.combineWith(a) }


fun <A: Semigroup<A>> Monoid<A>.dual() = monoid(Dual(empty))

class Endo<A>(private val f: (A) -> A): Semigroup<Endo<A>> {
	operator fun invoke(a: A) = f(a)
	override fun combineWith(other: Endo<A>) = Endo(f.compose(other.f))
}

fun <A> endoMonoid() = monoid(Endo(id<A>()))


