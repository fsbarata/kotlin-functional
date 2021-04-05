package com.github.fsbarata.functional.data.monoid

import com.github.fsbarata.functional.data.*

class Endo<A>(private val f: (A) -> A): F1<A, A> by f,
	Semigroup<Endo<A>> {
	override fun combineWith(other: Endo<A>) = Endo(f.compose(other.f))
}

fun <A> endoMonoid() = monoid(Endo(id<A>()))
