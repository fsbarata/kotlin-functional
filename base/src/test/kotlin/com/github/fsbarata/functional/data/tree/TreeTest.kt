package com.github.fsbarata.functional.data.tree

import com.github.fsbarata.functional.control.ComonadLaws
import com.github.fsbarata.functional.control.MonadZipLaws
import com.github.fsbarata.functional.data.Functor
import com.github.fsbarata.functional.data.TraversableLaws

class TreeTest:
	MonadZipLaws<TreeContext>,
	ComonadLaws<TreeContext>,
	TraversableLaws<TreeContext> {
	override val monadScope = Tree
	override val traversableScope = Tree

	override val possibilities: Int = 10
	override fun factory(possibility: Int) = createTreeSequence(possibility)

	override fun <A> createTraversable(vararg items: A) =
		Tree(
			items[0],
			when (items.size) {
				1 -> emptySequence()
				2 -> sequenceOf(Tree(items[1]))
				else -> sequenceOf(
					Tree(items[1]),
					Tree(items[2], items.asSequence().drop(3).map(::Tree))
				)
			}
		)

	override fun <A> Functor<TreeContext, A>.equalTo(other: Functor<TreeContext, A>): Boolean =
		asTree.root == other.asTree.root &&
				asTree.sub.toList().equalTo(other.asTree.sub.toList())

	private fun <A> List<Tree<A>>.equalTo(other: List<Tree<A>>) =
		size == other.size && zip(other).all { (t1, t2) -> t1.equalTo(t2) }
}
