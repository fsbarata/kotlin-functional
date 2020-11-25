package com.github.fsbarata.functional.control.reader

import com.github.fsbarata.functional.Context
import com.github.fsbarata.functional.control.Applicative
import com.github.fsbarata.functional.control.Monad
import com.github.fsbarata.functional.control.arrow.kleisli
import com.github.fsbarata.functional.data.id

/**
 * Reader Monad
 *
 * This type models a set of dependencies D, required to generate the value A.
 */
class Reader<D, out A>(val runReader: (D) -> A):
	Monad<ReaderContext<D>, A> {
	override val scope get() = Scope<D>()

	override fun <B> map(f: (A) -> B): Reader<D, B> =
		Reader { f(runReader(it)) }

	override infix fun <B> ap(ff: Applicative<ReaderContext<D>, (A) -> B>): Reader<D, B> =
		Reader { d -> ff.map { it(runReader(d)) }.asReader.runReader(d) }

	override infix fun <B> bind(f: (A) -> Context<ReaderContext<D>, B>): Reader<D, B> =
		flatMap { f(it).asReader }

	fun <B> flatMap(f: (A) -> Reader<in D, B>) =
		Reader<D, B> { f(runReader(it)).runReader(it) }

	fun <B> using(f: (B) -> D) = Reader<B, A> { b -> runReader(f(b)) }

	operator fun invoke(d: D) = runReader(d)

	class Scope<D>: Monad.Scope<ReaderContext<D>> {
		override fun <A> just(a: A) = just<D, A>(a)
	}

	companion object {
		fun <D, A> just(a: A): Reader<D, A> = Reader { a }

		fun <D> ask(): Reader<D, D> = Reader(::id)

		fun <D, A, B> kleisli(f: (A) -> Reader<D, B>) = Scope<D>().kleisli(f)
	}
}

internal typealias ReaderContext<D> = Reader<D, *>

val <D, A> Context<ReaderContext<D>, A>.asReader
	get() = this as Reader<D, A>
