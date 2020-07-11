package com.fsbarata.fp.types

import com.fsbarata.fp.concepts.*

data class Option<A>(
		val value: A?
) : Monad<Any, A>,
		Foldable<A> {
	override fun <B> just(b: B) = Option.just(b)

	override fun <B> flatMap(f: (A) -> Functor<Any, B>) =
			Option(value?.let { f(it).asOption.value })

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

val <A> Context<Any, A>.asOption
	get() = this as Option<A>

fun <A> Monoid<A>.option() = object : Monoid<Option<A>> {
	override fun empty() = Option.empty<A>()

	override fun Option<A>.combine(a: Option<A>): Option<A> =
			flatMap { t -> a.map { t.combine(it) } }
}