package com.github.fsbarata.functional.control.trans

import com.github.fsbarata.functional.control.Monad
import com.github.fsbarata.functional.control.MonadLaws
import com.github.fsbarata.functional.control.monad.MonadTrans
import com.github.fsbarata.functional.data.composeForward
import com.github.fsbarata.functional.data.list.ListF
import kotlin.test.Test

interface MonadTransLaws<T>: MonadLaws<Monad<T, ListF<*>>> {
	override val monadScope: MonadTrans.Scope<T, ListF<*>>

	@Test
	fun `lift after just`() {
		assertEqualF(monadScope.just(5), monadScope.lift(ListF.just(5)))
	}

	@Test
	fun `lift bind`() {
		val list = ListF.of(3, 4, 6, 8)
		val f = { a: Int -> ListF.of((a - 3).toString(), (a + 5).toString()) }
		assertEqualF(monadScope.lift(list).bind(f composeForward monadScope::lift), monadScope.lift(list.bind(f)))
	}
}
