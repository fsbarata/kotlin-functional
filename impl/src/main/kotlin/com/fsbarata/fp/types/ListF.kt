package com.fsbarata.fp.types

import com.fsbarata.fp.concepts.Context
import com.fsbarata.fp.concepts.Foldable
import com.fsbarata.fp.concepts.Functor
import com.fsbarata.fp.concepts.Monad

class ListF<A>(
		private val wrapped: List<A>
) : Monad<List<*>, A>,
		Foldable<A>,
		List<A> by wrapped {
	override fun <B> just(b: B): ListF<B> = Companion.just(b)

	override fun <B> map(f: (A) -> B) =
			wrapped.map(f).f()

	override fun <B> ap(ff: Functor<List<*>, (A) -> B>): ListF<B> =
			flatMap { item -> ff.map { it(item) } }

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

val <A> Context<List<*>, A>.asList: ListF<A>
	get() = this as ListF<A>

