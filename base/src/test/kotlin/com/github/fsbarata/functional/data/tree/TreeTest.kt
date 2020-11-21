package com.github.fsbarata.functional.data.tree

import com.github.fsbarata.functional.control.ComonadLaws
import com.github.fsbarata.functional.control.MonadZipLaws
import com.github.fsbarata.functional.data.TraversableLaws

class TreeTest:
	MonadZipLaws<TreeContext>,
	ComonadLaws<TreeContext>,
	TraversableLaws<TreeContext> {
	override val monadScope = Tree
	override val traversableScope = Tree

	override fun <A> createComonad(a: A) = Tree(a)

	override val possibilities: Int = 10
	override fun factory(possibility: Int) = createTreeSequence(possibility).toTree()

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