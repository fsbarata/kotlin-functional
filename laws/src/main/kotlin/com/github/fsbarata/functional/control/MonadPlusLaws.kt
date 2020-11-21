package com.github.fsbarata.functional.control

import org.junit.Test

interface MonadPlusLaws<M>: MonadLaws<M> {
	override val monadScope: MonadPlus.Scope<M>

	private fun <A> create(a: A): MonadPlus<M, A> =
		monadScope.just(a) as MonadPlus<M, A>

	private val empty get() = monadScope.empty<Any>() as MonadPlus<M, Any>

	@Test
	fun `left zero`() {
		val v1 = create(4)
		val v2 = create(2).associateWith(create(4))
		assertEqualF(empty, empty.bind { v1 })
		assertEqualF(empty, empty.bind { v2 })
	}

	@Test
	fun `right zero`() {
		val v1 = create(4)
		val v2 = create(2).associateWith(create(4)) as MonadPlus<M, Int>
		assertEqualF(empty, v1.bind { empty })
		assertEqualF(empty, v2.bind { empty })
	}

	@Test
	fun `filter from bind`() {
		val f = { a: Int -> a > 3 }
		val v1 = create(4)
		val v2 = create(2).associateWith(create(4)) as MonadPlus<M, Int>
		assertEqualF(v1.filterFromBind(f), v1.filter(f))
		assertEqualF(v2.filterFromBind(f), v2.filter(f))
	}
}