package com.github.fsbarata.functional.control.monad

import com.github.fsbarata.functional.Context
import com.github.fsbarata.functional.control.MonadPlusLaws
import com.github.fsbarata.functional.control.MonadZipScopeLaws
import com.github.fsbarata.functional.control.andThen
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
	MonadPlusLaws<Context<ListContext, OptionalContext>>,
	MonadZipScopeLaws<Context<ListContext, OptionalContext>> {
	override val possibilities: Int = 4
	override val monadScope = OptionalT(ListF)

	override fun factory(possibility: Int) = when (possibility) {
		0 -> OptionalT(ListF).empty()
		1 -> OptionalT(ListF.of(Optional.just(1), Optional.just(2)))
		2 -> OptionalT(ListF.of(Optional.just(3), Optional.empty()))
		else -> monadScope.just(possibility)
	}

	@Test
	fun mapOptionalT() {
		assertEquals(
			OptionalT(ListF).just(3),
			OptionalT(Identity).just(3).mapOptionalT { ListF.just(it.asIdentity.a) }
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
	fun flatMapT() {
		assertEquals(
			OptionalT(
				ListF.of(
					Optional.just("1"),
					Optional.just("6"), Optional.empty(),
					Optional.just("3"),
					Optional.empty(),
				)
			),
			OptionalT(ListF.of(Optional.just(1), Optional.just(5), Optional.just(3), Optional.empty())).flatMapT {
				if (it < 4) ListF.just(Optional.just(it.toString()))
				else ListF.of(Optional.just((it + 1).toString()), None)
			}
		)
	}

	@Test
	fun flatMap() {
		assertEquals(
			OptionalT(
				ListF.of(
					Optional.just("1"),
					Optional.just("6"), Optional.empty(),
					Optional.just("3"),
					Optional.empty(),
				)
			),
			OptionalT(ListF.of(Optional.just(1), Optional.just(5), Optional.just(3), Optional.empty())).flatMap {
				if (it < 4) OptionalT(ListF).just(it.toString())
				else OptionalT(ListF.of(Optional.just((it + 1).toString()), None))
			}
		)
	}

	@Test
	fun zip() {
		assertEquals(
			OptionalT(ListF.of(Optional.just("3"), Optional.empty(), Optional.empty(), Optional.just("6"))),
			zip(
				OptionalT(
					ListF.of(
						Optional.just(2),
						Optional.empty(),
						Optional.just(4),
						Optional.just(3),
						Optional.just(4)
					)
				),
				OptionalT(ListF.of(Optional.just(1), Optional.just(2), Optional.empty(), Optional.just(3)))
			) { a, b -> (a + b).toString() }
		)
	}

	override fun `right zero`() {
		assertEquals(
			OptionalT(ListF.of(Optional.empty<Int>())),
			OptionalT(ListF.of(Optional.empty<Int>())).andThen(OptionalT(ListF).empty()),
		)
		assertEquals(
			OptionalT(ListF.just(Optional.empty<Int>())),
			OptionalT(ListF.just(Optional.just(3))).andThen(OptionalT(ListF).empty()),
		)
		assertEquals(
			OptionalT(ListF.of(Optional.empty<Int>(), Optional.empty())),
			OptionalT(ListF.of(Optional.just(3), Optional.just(5))).andThen(OptionalT(ListF).empty()),
		)
	}
}
