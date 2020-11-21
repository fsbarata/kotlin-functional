package com.github.fsbarata.functional.data.tree

import com.github.fsbarata.functional.control.Comonad
import com.github.fsbarata.functional.control.ComonadLaws
import com.github.fsbarata.functional.control.MonadZipLaws
import com.github.fsbarata.functional.data.Functor
import com.github.fsbarata.functional.data.TraversableLaws

class TreeSequenceTest:
	MonadZipLaws<TreeSequenceContext>,
	ComonadLaws<TreeSequenceContext>,
	TraversableLaws<TreeSequenceContext> {
	override val monadScope = TreeSequence
	override val traversableScope = TreeSequence

	override val possibilities: Int = 10
	override fun factory(possibility: Int) = createTreeSequence(possibility)

	override fun <A> createComonad(a: A) = TreeSequence.just(a)

	override fun <A> createTraversable(vararg items: A) =
		TreeSequence(
			items[0],
			when (items.size) {
				1 -> emptySequence()
				2 -> sequenceOf(TreeSequence(items[1]))
				else -> sequenceOf(
					TreeSequence(items[1]),
					TreeSequence(items[2], items.asSequence().drop(3).map(::TreeSequence))
				)
			}
		)

	override fun <A> Functor<TreeSequenceContext, A>.equalTo(other: Functor<TreeSequenceContext, A>) =
		asTreeSequence.toTree() == other.asTreeSequence.toTree()
}
