package com.github.fsbarata.functional.control.monad.state

import com.github.fsbarata.functional.control.Context
import com.github.fsbarata.functional.control.Monad

class State<S, A>(
	val runState: (S) -> Pair<S, A>,
): Monad<State<S, *>, A> {
	override val scope get() = StateScope<S>()

	override fun <B> map(f: (A) -> B): State<S, B> =
		State { s ->
			val (newState, value) = runState(s)
			Pair(newState, f(value))
		}

	override fun <B> bind(f: (A) -> Context<State<S, *>, B>): State<S, B> =
		State { s ->
			val (newState, value) = runState(s)
			f(value).asState.runState(newState)
		}

	class StateScope<S>: Monad.Scope<State<S, *>> {
		override fun <A> just(a: A) = just<S, A>(a)

		fun get() = State { s: S -> Pair(s, s) }
		fun put(newState: S) = State<S, Unit> { Pair(newState, Unit) }
	}

	companion object {
		fun <S, A> just(a: A) = State<S, A> { s -> Pair(s, a) }
	}
}

fun <S> getState(): State<S, S> = State { s -> Pair(s, s) }
fun <S> putState(newState: S) = State<S, Unit> { Pair(newState, Unit) }

val <S, A> Context<State<S, *>, A>.asState
	get() = this as State<S, A>
