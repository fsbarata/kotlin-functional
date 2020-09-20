package com.fsbarata.fp.types

import com.fsbarata.fp.concepts.Context
import com.fsbarata.fp.concepts.Foldable
import com.fsbarata.fp.concepts.Functor
import com.fsbarata.fp.concepts.Monad

data class ListF<A>(
		private val wrapped: List<A>
) : Monad<ListF<*>, A>,
		Foldable<A>,
		List<A> by wrapped {
	override fun <B> just(b: B): ListF<B> = Companion.just(b)

	override fun <B> map(f: (A) -> B) =
			wrapped.map(f).f()

	override fun <B> ap(ff: Functor<ListF<*>, (A) -> B>): ListF<B> =
			bind { item -> ff.map { it(item) } }

	override fun <B> bind(f: (A) -> Functor<ListF<*>, B>) =
			flatMap { f(it).asList }

	fun <B> flatMap(f: (A) -> List<B>) =
			wrapped.flatMap(f).f()

	override fun <R> fold(initialValue: R, accumulator: (R, A) -> R): R =
			wrapped.fold(initialValue, accumulator)

	companion object {
		fun <A> empty() = emptyList<A>().f()
		fun <A> just(a: A) = listOf(a).f()
	}
}

fun <A> List<A>.f() = ListF(this)

val <A> Context<ListF<*>, A>.asList: ListF<A>
	get() = this as ListF<A>

