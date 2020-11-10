package com.github.fsbarata.functional.data.tree

import com.github.fsbarata.functional.control.Comonad
import com.github.fsbarata.functional.control.test.ComonadLaws
import com.github.fsbarata.functional.control.test.MonadLaws
import com.github.fsbarata.functional.control.test.MonadZipLaws
import com.github.fsbarata.functional.data.test.TraversableLaws

class TreeTest:
	MonadLaws<TreeContext>,
	MonadZipLaws<TreeContext>,
	ComonadLaws<TreeContext>,
	TraversableLaws<TreeContext> {
	override val monadScope = Tree
	override val traversableScope = Tree

	override fun <A> createFunctor(a: A) = Tree.just(a)


	override fun <A> createTraversable(vararg items: A) =
		Tree(
			items[0],
			when (items.size) {
				1 -> emptyList()
				2 -> listOf(Tree(items[1]))
				else -> listOf(Tree(items[1]), Tree(items[2], items.drop(3).map(::Tree)))
			}
		)

}