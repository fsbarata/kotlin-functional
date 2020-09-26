package com.fsbarata.fp.types

import com.fsbarata.fp.concepts.Context
import com.fsbarata.fp.concepts.Functor
import com.fsbarata.fp.concepts.Monad

class State<S, A>(
	val runState: (S) -> Pair<S, A>,
): Monad<State<S, *>, A> {
	override fun <B> just(b: B) = Companion.just<S, B>(b)

	override fun <B> map(f: (A) -> B): State<S, B> =
		State { s ->
			val (newState, value) = runState(s)
			Pair(newState, f(value))
		}

	override fun <B> bind(f: (A) -> Functor<State<S, *>, B>): State<S, B> =
		State { s ->
			val (newState, value) = runState(s)
			f(value).asState.runState(newState)
		}

	companion object {
		fun <S, A> just(a: A) = State<S, A> { s -> Pair(s, a) }
	}
}

fun <S> getState(): State<S, S> = State { s -> Pair(s, s) }
fun <S> putState(newState: S) = State<S, Unit> { Pair(newState, Unit) }

val <S, A> Context<State<S, *>, A>.asState
	get() = this as State<S, A>
