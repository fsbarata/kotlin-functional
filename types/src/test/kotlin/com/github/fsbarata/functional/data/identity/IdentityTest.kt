package com.github.fsbarata.functional.data.identity

import com.github.fsbarata.functional.control.Applicative
import com.github.fsbarata.functional.control.test.ComonadLaws
import com.github.fsbarata.functional.control.test.MonadZipLaws
import com.github.fsbarata.functional.data.test.FoldableLaws

class IdentityTest
	: MonadZipLaws<IdentityContext>,
	FoldableLaws,
	ComonadLaws<IdentityContext> {
	override val monadScope = Identity
	override fun <A> createFunctor(a: A) = Identity(a)

	override fun <A> createFoldable(vararg items: A) =
		Identity(items.first())
}
