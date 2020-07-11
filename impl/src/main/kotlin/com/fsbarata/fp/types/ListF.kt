package com.fsbarata.fp.types

import com.fsbarata.fp.concepts.*

class ListF<A>(
		private val wrapped: List<A>
) : Monad<List<*>, A>,
		Foldable<A>,
		List<A> by wrapped {
	override fun <B> just(b: B): ListF<B> = Companion.just(b)

	override fun <B> map(f: (A) -> B) =
			wrapped.map(f).f()

	override fun <B> flatMap(f: (A) -> Functor<List<*>, B>) =
			wrapped.flatMap { f(it).asList }.f()

	override fun <R> fold(initialValue: R, accumulator: (R, A) -> R): R =
			wrapped.fold(initialValue, accumulator)

	companion object {
		fun <A> empty() = emptyList<A>().f()
		fun <A> just(a: A) = listOf(a).f()
	}
}

fun <A> List<A>.f() = ListF(this)
fun <A> List<A>.monoid() = object : Monoid<List<A>> {
	override fun empty() = emptyList<A>()
	override fun List<A>.combine(a: List<A>) = this + a
}

val <A> Context<List<*>, A>.asList: List<A>
	get() = this as List<A>

