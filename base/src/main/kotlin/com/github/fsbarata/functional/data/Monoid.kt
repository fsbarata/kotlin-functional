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

class MonoidSemigroupFactory<A>(val monoid: Monoid<A>) {
	fun wrap(a: A) = WrappedMonoid(a)
	operator fun invoke(a: A) = wrap(a)

	inner class WrappedMonoid(private val a: A): Semigroup<WrappedMonoid> {
		fun unwrap() = a

		override fun combineWith(other: WrappedMonoid): WrappedMonoid =
			wrap(monoid.combine(a, other.a))

		override fun toString() = "Wrapped($a)"

		override fun equals(other: Any?): Boolean {
			if (this === other) return true
			return other is MonoidSemigroupFactory<*>.WrappedMonoid && a == other.a
		}

		override fun hashCode() = a.hashCode()
	}
}

fun <A> Monoid<A>.semigroupFactory() = MonoidSemigroupFactory(this)

fun <A: Semigroup<A>> Monoid<A>.dual() = monoid(Dual(empty))

class Endo<A>(private val f: (A) -> A): Semigroup<Endo<A>> {
	operator fun invoke(a: A) = f(a)
	override fun combineWith(other: Endo<A>) = Endo(f.compose(other.f))
}

fun <A> endoMonoid() = monoid(Endo(id<A>()))


