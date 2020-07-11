package com.fsbarata.fp.types

import com.fsbarata.fp.concepts.Context
import com.fsbarata.fp.concepts.Functor
import com.fsbarata.fp.concepts.Monad

class Reader<D, A>(val run: (D) -> A) : Monad<Reader<D, *>, A> {
	override fun <B> just(b: B): Reader<D, B> = Companion.just(b)

	override fun <B> map(f: (A) -> B): Reader<D, B> =
			Reader { f(run(it)) }

	override fun <B> ap(ff: Functor<Reader<D, *>, (A) -> B>): Reader<D, B> =
			Reader { d -> ff.map { it(run(d)) }.asReader.run(d) }

	override fun <B> flatMap(f: (A) -> Functor<Reader<D, *>, B>): Reader<D, B> =
			Reader { f(run(it)).asReader.run(it) }

	companion object {
		fun <D, A> just(a: A): Reader<D, A> = Reader { a }

		fun <D> ask(): Reader<D, D> = Reader { it }
	}
}

val <D, A> Context<Reader<D, *>, A>.asReader
	get() = this as Reader<D, A>
