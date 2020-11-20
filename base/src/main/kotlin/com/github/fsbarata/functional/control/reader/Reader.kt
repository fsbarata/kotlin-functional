package com.github.fsbarata.functional.control.reader

import com.github.fsbarata.functional.control.Applicative
import com.github.fsbarata.functional.Context
import com.github.fsbarata.functional.control.Monad
import com.github.fsbarata.functional.data.id

/**
 * Reader Monad
 *
 * This object models a set of dependencies D, required to generate the value A.
 */
class Reader<D, out A>(val runReader: (D) -> A): Monad<ReaderContext<D>, A> {
	override val scope get() = ReaderScope<D>()

	override fun <B> map(f: (A) -> B): Reader<D, B> =
		Reader { f(runReader(it)) }

	override infix fun <B> ap(ff: Applicative<ReaderContext<D>, (A) -> B>): Reader<D, B> =
		Reader { d -> ff.map { it(runReader(d)) }.asReader.runReader(d) }

	override infix fun <B> bind(f: (A) -> Context<ReaderContext<D>, B>): Reader<D, B> =
		flatMap { f(it).asReader }

	fun <B> flatMap(f: (A) -> Reader<in D, B>) =
		Reader<D, B> { f(runReader(it)).runReader(it) }

	fun <E> using(f: (E) -> D): Reader<E, A> =
		Reader { e -> runReader(f(e)) }

	operator fun invoke(d: D) = runReader(d)

	class ReaderScope<D>: Monad.Scope<ReaderContext<D>> {
		override fun <A> just(a: A) = just<D, A>(a)
	}

	companion object {
		fun <D, A> just(a: A): Reader<D, A> = Reader { a }

		fun <D> ask(): Reader<D, D> = Reader(::id)
	}
}

internal typealias ReaderContext<D> = Reader<D, *>

val <D, A> Context<ReaderContext<D>, A>.asReader
	get() = this as Reader<D, A>
