package com.github.fsbarata.functional.data

interface Monoid<A>: Semigroup.Scope<A> {
	val empty: A
}

fun <A: Semigroup<A>> monoid(empty: A) = semigroupScopeOf<A>().monoid(empty)

fun <A> Semigroup.Scope<A>.monoid(empty: A): Monoid<A> =
	object: Monoid<A>, Semigroup.Scope<A> by this@monoid {
		override val empty: A = empty
	}

inline fun <A> monoid(empty: A, semigroup: Semigroup.Scope<A>) = semigroup.monoid(empty)

class MonoidSemigroupFactory<A>(val monoid: Monoid<A>) {
	fun wrap(a: A) = WrappedMonoid(a)
	operator fun invoke(a: A) = wrap(a)

	inner class WrappedMonoid(private val a: A): Semigroup<WrappedMonoid> {
		fun unwrap() = a

		override fun concatWith(other: WrappedMonoid): WrappedMonoid =
			wrap(monoid.concat(a, other.a))

		override fun toString() = "Wrapped($a)"

		override fun equals(other: Any?): Boolean {
			if (this === other) return true
			return other is MonoidSemigroupFactory<*>.WrappedMonoid && a == other.a
		}

		override fun hashCode() = a.hashCode()
	}
}

fun <A> Monoid<A>.semigroupFactory() = MonoidSemigroupFactory(this)


