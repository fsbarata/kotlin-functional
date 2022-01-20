package com.github.fsbarata.functional.control.state

import com.github.fsbarata.functional.Context
import com.github.fsbarata.functional.assertEquals
import com.github.fsbarata.functional.control.MonadLaws
import com.github.fsbarata.functional.data.tuple.Tuple2
import kotlin.test.Test

private typealias MyState = Pair<String, Int>

class StateTest:
	MonadLaws<StateContext<MyState>> {
	override val monadScope = State.Scope<MyState>()


	override val possibilities: Int = 10
	override fun factory(possibility: Int) =
		State<MyState, Int> { (str, num) ->
			Tuple2(
				MyState(str.map { it + possibility }.toCharArray().concatToString(), num),
				str.hashCode() - num
			)
		}

	private val basicState = Pair("ab", -38)

	override fun <A> Context<StateContext<MyState>, A>.equalTo(other: Context<StateContext<MyState>, A>) =
		asState.runState(basicState) == other.asState.runState(basicState)

	@Test
	fun eval() {
		assertEquals(
			5,
			State.gets { s: Int -> s + 1 }
				.eval(4)
		)
	}

	@Test
	fun exec() {
		assertEquals(
			5,
			State.modify { s: Int -> s + 1 }
				.exec(4)
		)
	}

	@Test
	fun map() {
		assertEquals(
			Tuple2(4, 10),
			State.gets { s: Int -> s + 1 }
				.map { it * 2 }
				.runState(4)
		)
	}

	@Test
	fun flatMap() {
		assertEquals(
			Tuple2(4, 14),
			State.gets { s: Int -> s + 1 }
				.flatMap { a -> State.gets { s: Int -> (a * 2) + s } }
				.runState(4)
		)
	}

	@Test
	fun get() {
		assertEquals(
			Tuple2(10, 10),
			State.get<Int>().runState(10)
		)
	}

	@Test
	fun gets() {
		assertEquals(
			Tuple2(10, "10 + 3"),
			State.gets { a: Int -> "$a + 3" }.runState(10)
		)
	}

	@Test
	fun put() {
		assertEquals(
			Tuple2(12, Unit),
			State.put(12).runState(10)
		)
	}

	@Test
	fun modify() {
		assertEquals(
			Tuple2(13, Unit),
			State.modify<Int> { a -> a + 3 }.runState(10)
		)
	}
}