package com.github.fsbarata.functional.data.identity

import com.github.fsbarata.functional.control.ComonadLaws
import com.github.fsbarata.functional.control.MonadZipLaws
import com.github.fsbarata.functional.data.FoldableLaws

class IdentityTest:
	MonadZipLaws<IdentityContext>,
	ComonadLaws<IdentityContext>,
	FoldableLaws {
	override val monadScope = Identity

	override val possibilities: Int = 2
	override fun factory(possibility: Int) = Identity(possibility)

	override fun <A> createFoldable(vararg items: A) =
		Identity(items.first())
}
