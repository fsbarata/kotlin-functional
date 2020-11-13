package com.github.fsbarata.functional.control.monad.state

import com.github.fsbarata.functional.control.test.MonadLaws
import com.github.fsbarata.functional.data.Functor

private typealias MyState = Pair<String, Int>

class StateTest:
	MonadLaws<StateContext<MyState>> {
	override val monadScope = State.StateScope<MyState>()

	private val basicState = Pair("ab", -38)

	override fun <A> Functor<StateContext<MyState>, A>.equalTo(other: Functor<StateContext<MyState>, A>) =
		asState.runState(basicState) == other.asState.runState(basicState)
}