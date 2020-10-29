package com.fsbarata.fp.types

import com.fsbarata.fp.concepts.Applicative
import com.fsbarata.fp.concepts.Context
import com.fsbarata.fp.concepts.Foldable
import com.fsbarata.fp.concepts.Monad
import com.fsbarata.fp.concepts.MonadZip
import java.io.Serializable

data class ListF<A>(
	private val wrapped: List<A>,
): Monad<ListF<*>, A>,
   MonadZip<ListF<*>, A>,
   Foldable<A>,
   List<A> by wrapped,
   Serializable {
	override val scope get() = Companion

	override inline fun <B> map(f: (A) -> B) =
		(this as List<A>).map(f).f()

	override fun <B> ap(ff: Applicative<ListF<*>, (A) -> B>): ListF<B> =
		bind { item -> ff.map { it(item) } }

	override fun <B> bind(f: (A) -> Context<ListF<*>, B>) =
		flatMap { f(it).asList }

	inline fun <B> flatMap(f: (A) -> List<B>) =
		(this as List<A>).flatMap(f).f()

	override fun <R> fold(initialValue: R, accumulator: (R, A) -> R): R =
		wrapped.fold(initialValue, accumulator)

	override fun <B, R> zipWith(other: MonadZip<ListF<*>, B>, f: (A, B) -> R): ListF<R> =
		zip(other.asList, f).f()

	companion object: Monad.Scope<ListF<*>> {
		fun <A> empty() = emptyList<A>().f()
		override fun <A> just(a: A) = listOf(a).f()
	}
}

fun <A> List<A>.f() = ListF(this)

val <A> Context<ListF<*>, A>.asList: ListF<A>
	get() = this as ListF<A>

