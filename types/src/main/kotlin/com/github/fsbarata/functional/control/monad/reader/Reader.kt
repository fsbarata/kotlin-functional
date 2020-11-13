package com.github.fsbarata.functional.control.monad.reader

import com.github.fsbarata.functional.control.Applicative
import com.github.fsbarata.functional.Context
import com.github.fsbarata.functional.control.Monad

/**
 * Reader Monad
 *
 * This object models a set of dependencies D, required to generate the value A.
 */
class Reader<D, out A>(val run: (D) -> A): Monad<ReaderContext<D>, A> {
	override val scope get() = ReaderScope<D>()

	override fun <B> map(f: (A) -> B): Reader<D, B> =
		Reader { f(run(it)) }

	override infix fun <B> ap(ff: Applicative<ReaderContext<D>, (A) -> B>): Reader<D, B> =
		Reader { d -> ff.map { it(run(d)) }.asReader.run(d) }

	override infix fun <B> bind(f: (A) -> Context<ReaderContext<D>, B>): Reader<D, B> =
		flatMap { f(it).asReader }

	fun <B> flatMap(f: (A) -> Reader<in D, B>) =
		Reader<D, B> { f(run(it)).run(it) }

	fun <E> leftMap(f: (E) -> D): Reader<E, A> =
		Reader { e -> run(f(e)) }

	operator fun invoke(d: D) = run(d)

	class ReaderScope<D>: Monad.Scope<ReaderContext<D>> {
		override fun <A> just(a: A) = just<D, A>(a)
	}

	companion object {
		fun <D, A> just(a: A): Reader<D, A> = Reader { a }

		fun <D> ask(): Reader<D, D> = Reader { it }
	}
}

internal typealias ReaderContext<D> = Reader<D, *>

val <D, A> Context<ReaderContext<D>, A>.asReader
	get() = this as Reader<D, A>
