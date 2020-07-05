package com.fsbarata.fp.types

import com.fsbarata.fp.concepts.Functor
import com.fsbarata.fp.concepts.Context
import com.fsbarata.fp.concepts.Monad

class ListF<A>(
		private val wrapped: List<A>
) : Monad<List<*>, A>,
		List<A> by wrapped {
	override fun <B> just(b: B): ListF<B> = Companion.just(b)

	override fun <B> map(f: (A) -> B) =
			wrapped.map(f).monad()

	override fun <B> flatMap(f: (A) -> Functor<List<*>, B>) =
			wrapped.flatMap { f(it).value }.monad()

	fun toList() = wrapped

	companion object {
		fun <A> just(a: A) = listOf(a).monad()
	}
}

fun <A> List<A>.monad() = ListF(this)

val <A> Context<List<*>, A>.value: List<A>
	get() = (this as ListF<A>).toList()

