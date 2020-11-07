package com.github.fsbarata.functional.data.identity

import com.github.fsbarata.functional.control.Functor
import com.github.fsbarata.functional.control.test.MonadZipTest
import com.github.fsbarata.functional.data.test.FoldableTest

class IdentityTest: MonadZipTest<Identity<*>>, FoldableTest {
	override val monadScope = Identity
	override fun <A> Functor<Identity<*>, A>.equalTo(other: Functor<Identity<*>, A>) =
		asIdentity.a == other.asIdentity.a

	override fun <A> createFoldable(vararg items: A) =
		Identity(items.first())
}
