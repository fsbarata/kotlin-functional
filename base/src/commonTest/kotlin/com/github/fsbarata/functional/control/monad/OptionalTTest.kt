package com.github.fsbarata.functional.control.monad

import com.github.fsbarata.functional.control.Monad
import com.github.fsbarata.functional.control.MonadPlusLaws
import com.github.fsbarata.functional.control.trans.MonadTransLaws
import com.github.fsbarata.functional.data.identity.Identity
import com.github.fsbarata.functional.data.identity.asIdentity
import com.github.fsbarata.functional.data.list.ListContext
import com.github.fsbarata.functional.data.list.ListF
import com.github.fsbarata.functional.data.maybe.None
import com.github.fsbarata.functional.data.maybe.Optional
import com.github.fsbarata.functional.data.maybe.OptionalContext
import kotlin.test.Test
import kotlin.test.assertEquals

class OptionalTTest:
	MonadTransLaws<OptionalContext>,
	MonadPlusLaws<Monad<ListContext, OptionalContext>> {
	override val possibilities: Int = 4
	override val monadScope = OptionalT.Scope(ListF)

	override fun factory(possibility: Int) = when (possibility) {
		0 -> OptionalT(ListF.just(None))
		1 -> OptionalT(ListF.of(Optional.just(1), Optional.just(2)))
		2 -> OptionalT(ListF.of(Optional.just(3), Optional.empty()))
		else -> monadScope.just(possibility)
	}

	@Test
	fun mapOptionalT() {
		assertEquals(
			OptionalT(ListF.of(Optional.just(3))),
			OptionalT(Identity(Optional.just(3))).mapOptionalT { ListF.just(it.asIdentity.a) }
		)
	}

	@Test
	fun map() {
		assertEquals(
			OptionalT(ListF.of(Optional.just("3"), Optional.just("5"), Optional.empty())),
			OptionalT(ListF.of(Optional.just(3), Optional.just(5), Optional.empty())).map { it.toString() }
		)
	}

	@Test
	fun flatMap() {
		assertEquals(
			OptionalT(ListF.of(
				Optional.just("1"),
				Optional.just("6"), Optional.empty(),
				Optional.just("3"),
				Optional.empty(),
			)),
			OptionalT(ListF.of(Optional.just(1), Optional.just(5), Optional.just(3), Optional.empty())).flatMap {
				if (it < 4) OptionalT.Scope(ListF).just(it.toString())
				else OptionalT(ListF.of(Optional.just((it + 1).toString()), None))
			}
		)
	}
}
