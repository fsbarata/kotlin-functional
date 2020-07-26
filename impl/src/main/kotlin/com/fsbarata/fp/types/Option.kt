package com.fsbarata.fp.types

import com.fsbarata.fp.concepts.Context
import com.fsbarata.fp.concepts.Foldable
import com.fsbarata.fp.concepts.Functor
import com.fsbarata.fp.concepts.Monad

data class Option<A>(
		val value: A?
) : Monad<Option<*>, A>,
		Foldable<A> {
	infix fun or(a: A) = value ?: a
	infix fun orOption(a: Option<A>) = Option(value ?: a.value)

	override fun <B> just(b: B) = Option.just(b)

	override fun <B> map(f: (A) -> B) =
			Option(value?.let(f))

	override fun <B> ap(ff: Functor<Option<*>, (A) -> B>): Option<B> =
			ff.map { map(it) }.asOption.value ?: empty()

	override fun <B> flatMap(f: (A) -> Functor<Option<*>, B>) =
			Option(value?.let { f(it).asOption.value })

	fun <B> fold(ifEmpty: () -> B, ifSome: (A) -> B): B {
		return ifSome(value ?: return ifEmpty())
	}


	override fun <R> fold(initialValue: R, accumulator: (R, A) -> R): R {
		return accumulator(initialValue, value ?: return initialValue)
	}

	companion object {
		fun <A> empty() = Option<A>(null)
		fun <A> just(a: A) = Option(a)
	}
}

fun <A : Any> A?.toOption() = Option(this)
fun <A : Any> A?.f() = toOption()

val <A> Context<Option<*>, A>.asOption
	get() = this as Option<A>
