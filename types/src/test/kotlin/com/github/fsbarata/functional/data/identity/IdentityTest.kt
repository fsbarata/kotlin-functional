package com.github.fsbarata.functional.data.identity

import com.github.fsbarata.functional.control.Functor
import com.github.fsbarata.functional.control.test.MonadZipLaws
import com.github.fsbarata.functional.data.test.FoldableLaws

class IdentityTest: MonadZipLaws<Identity<*>>, FoldableLaws {
	override val monadScope = Identity
	override fun <A> Functor<Identity<*>, A>.equalTo(other: Functor<Identity<*>, A>) =
		asIdentity.a == other.asIdentity.a

	override fun <A> createFoldable(vararg items: A) =
		Identity(items.first())
}
