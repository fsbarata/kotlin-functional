package com.fsbarata.fp.types

import com.fsbarata.fp.concepts.Applicative
import com.fsbarata.fp.concepts.Context
import com.fsbarata.fp.concepts.Functor
import com.fsbarata.fp.concepts.Monad

class ProviderF<A>(
	private val get: () -> A,
): () -> A by get,
   Monad<ProviderF<*>, A> {
	override val scope get() = Companion

	override fun <B> map(f: (A) -> B): ProviderF<B> =
		ProviderF { f(get()) }

	override fun <B> ap(ff: Applicative<ProviderF<*>, (A) -> B>): ProviderF<B> =
		ProviderF { ff.map { it(get()) }.asProviderF.get() }

	override fun <B> bind(f: (A) -> Context<ProviderF<*>, B>): ProviderF<B> =
		flatMap { f(it).asProviderF }

	fun <B> flatMap(f: (A) -> ProviderF<B>): ProviderF<B> =
		f(get())

	companion object: Monad.Scope<ProviderF<*>> {
		override fun <A> just(a: A): ProviderF<A> = ProviderF { a }
	}
}

val <A> Context<ProviderF<*>, A>.asProviderF
	get() = this as ProviderF<A>

fun <A> (() -> A).f() = ProviderF(this)