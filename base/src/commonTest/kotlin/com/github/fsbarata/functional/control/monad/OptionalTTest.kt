package com.github.fsbarata.functional.control.monad

import com.github.fsbarata.functional.control.Monad
import com.github.fsbarata.functional.control.MonadPlusLaws
import com.github.fsbarata.functional.control.MonadZipLaws
import com.github.fsbarata.functional.control.trans.MonadTransLaws
import com.github.fsbarata.functional.data.Functor
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
	MonadPlusLaws<Monad<ListContext, OptionalContext>>,
	MonadZipLaws<Monad<ListContext, OptionalContext>> {
	override val possibilities: Int = 4
	override val monadScope = OptionalT(ListF)

	override fun factory(possibility: Int) = when (possibility) {
		0 -> OptionalT(ListF).empty()
		1 -> OptionalT(ListF.of(Optional.just(1), Optional.just(2)))
		2 -> OptionalT(ListF.of(Optional.just(3), Optional.empty()))
		else -> monadScope.just(possibility)
	}

	override fun <A, B, R> zip(
		arg1: Monad<Monad<ListContext, OptionalContext>, A>,
		arg2: Functor<Monad<ListContext, OptionalContext>, B>,
		f: (A, B) -> R
	) = arg1.asOptionalT.zipWith(arg2.asOptionalT, f)

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

	@Test
	fun zip() {
		assertEquals(
			OptionalT(ListF.of(Optional.just("3"), Optional.empty(), Optional.empty(), Optional.just("6"))),
			zip(
				OptionalT(ListF.of(Optional.just(2), Optional.empty(), Optional.just(4), Optional.just(3), Optional.just(4))),
				OptionalT(ListF.of(Optional.just(1), Optional.just(2), Optional.empty(), Optional.just(3)))
			) { a, b -> (a + b).toString() }
		)
	}
}
