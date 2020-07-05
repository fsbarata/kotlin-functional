package com.fsbarata.fp.types

import com.fsbarata.fp.concepts.Context
import com.fsbarata.fp.concepts.Functor
import com.fsbarata.fp.concepts.Monad

data class Option<A>(
		val value: A?
) : Monad<Any, A> {
	override fun <B> just(b: B) = Option.just(b)

	override fun <B> flatMap(f: (A) -> Functor<Any, B>) =
			Option(value?.let { (f(it) as Option<B>).value })

	companion object {
		fun <A> empty() = Option<A>(null)
		fun <A> just(a: A) = Option(a)
	}
}

fun <A : Any> A?.toOption() = Option(this)
fun <A : Any> A?.f() = toOption()

val <A> Context<Any, A>.asOption
	get() = this as Option<A>
