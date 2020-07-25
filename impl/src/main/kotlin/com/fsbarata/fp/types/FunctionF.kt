package com.fsbarata.fp.types

import com.fsbarata.fp.concepts.Context
import com.fsbarata.fp.concepts.Functor
import com.fsbarata.fp.concepts.Monad

class FunctionF<T, A>(
		private val tf: (T) -> A
) : (T) -> A by tf,
		Monad<FunctionF<T, *>, A> {
	override fun <B> just(b: B) =
			Companion.just<T, B>(b)

	override fun <B> map(f: (A) -> B): FunctionF<T, B> =
			FunctionF { f(tf(it)) }

	override fun <B> ap(ff: Functor<FunctionF<T, *>, (A) -> B>): FunctionF<T, B> =
			FunctionF { t -> ff.map { it(tf(t)) }.asFunF.tf(t) }

	override fun <B> flatMap(f: (A) -> Functor<FunctionF<T, *>, B>): FunctionF<T, B> =
			FunctionF { f(tf(it)).asFunF.tf(it) }

	companion object {
		fun <T, B> just(b: B): FunctionF<T, B> =
				FunctionF { b }
	}
}


val <T, A> Context<FunctionF<T, *>, A>.asFunF
	get() = this as FunctionF<T, A>
