package com.github.fsbarata.functional.control.concurrent

import com.github.fsbarata.functional.data.Functor
import com.github.fsbarata.functional.data.compose

interface SuspensionFunctor<F, A>: Functor<F, A>

@Suppress("OVERRIDE_BY_INLINE")
data class Yield<A>(val value: A): Functor<Yield<*>, A> {
	override inline fun <B> map(f: (A) -> B) = Yield(f(value))
}

@Suppress("OVERRIDE_BY_INLINE")
class Await<A>(val provider: () -> A): Functor<Await<*>, A> {
	override inline fun <B> map(f: (A) -> B) = Await(f compose provider)
}

@Suppress("OVERRIDE_BY_INLINE")
class Request<A, R>(val request: (R) -> A): Functor<Await<*>, A> {
	override inline fun <B> map(f: (A) -> B) = Request(f compose request)
}
