package com.fsbarata.fp.types

import com.fsbarata.fp.concepts.*

class ListF<A>(
		private val wrapped: List<A>
) : Monad<List<*>, A>,
		Monoid<ListF<A>>,
		List<A> by wrapped {
	override fun empty() = empty<A>()

	override fun <B> just(b: B): ListF<B> = Companion.just(b)

	override fun <B> map(f: (A) -> B) =
			wrapped.map(f).f()

	override fun <B> flatMap(f: (A) -> Functor<List<*>, B>) =
			wrapped.flatMap { f(it).asList }.f()

	override fun append(a: ListF<A>): ListF<A> =
			(this + a).f()

	companion object {
		fun <A> empty() = emptyList<A>().f()
		fun <A> just(a: A) = listOf(a).f()
	}
}

fun <A> List<A>.f() = ListF(this)

val <A> Context<List<*>, A>.asList: List<A>
	get() = this as List<A>

