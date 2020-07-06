package com.fsbarata.fp.types

import com.fsbarata.fp.concepts.Context
import com.fsbarata.fp.concepts.Functor
import com.fsbarata.fp.concepts.Monad
import com.fsbarata.fp.concepts.Semigroup

data class Option<A>(
		val value: A?
) : Monad<Any, A> {
	override fun <B> just(b: B) = Option.just(b)

	override fun <B> flatMap(f: (A) -> Functor<Any, B>) =
			Option(value?.let { f(it).asOption.value })

	companion object {
		fun <A> empty() = Option<A>(null)
		fun <A> just(a: A) = Option(a)
	}
}

fun <A : Any> A?.toOption() = Option(this)
fun <A : Any> A?.f() = toOption()

val <A> Context<Any, A>.asOption
	get() = this as Option<A>

data class OptionSemigroup<A : Semigroup<A>>(
		val value: A?
) : Semigroup<OptionSemigroup<A>> {
	override fun append(a: OptionSemigroup<A>): OptionSemigroup<A> =
			OptionSemigroup(value?.let { b -> a.value?.let(b::append) })
}

fun <A : Semigroup<A>> A.optionSemigroup(): OptionSemigroup<A> =
		OptionSemigroup(this)
