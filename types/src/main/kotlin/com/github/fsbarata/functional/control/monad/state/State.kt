package com.github.fsbarata.functional.control.monad.state

import com.github.fsbarata.functional.Context
import com.github.fsbarata.functional.control.Monad
import com.github.fsbarata.functional.data.tuple.Tuple2

class State<S, A>(
	val runState: (S) -> Tuple2<S, A>,
): Monad<StateContext<S>, A> {
	override val scope get() = StateScope<S>()

	override fun <B> map(f: (A) -> B): State<S, B> =
		State { s -> runState(s).map(f) }

	override infix fun <B> bind(f: (A) -> Context<StateContext<S>, B>): State<S, B> =
		flatMap { f(it).asState }

	fun <B> flatMap(f: (A) -> State<S, B>): State<S, B> =
		State { s ->
			val (newState, value) = runState(s)
			f(value).runState(newState)
		}

	class StateScope<S>: Monad.Scope<StateContext<S>> {
		override fun <A> just(a: A) = just<S, A>(a)

		fun get() = State { s: S -> Tuple2(s, s) }
		fun put(newState: S) = State<S, Unit> { Tuple2(newState, Unit) }
	}

	companion object {
		fun <S, A> just(a: A) = State<S, A> { s -> Tuple2(s, a) }
		fun <S> modify(f: (S) -> S) = State<S, Unit> { s -> Tuple2(f(s), Unit) }
		fun <S, A> gets(f: (S) -> A) = State<S, A> { s -> Tuple2(s, f(s)) }
	}
}

internal typealias StateContext<S> = State<S, *>

val <S, A> Context<StateContext<S>, A>.asState
	get() = this as State<S, A>
