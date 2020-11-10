package com.github.fsbarata.functional.data.tree

import com.github.fsbarata.functional.control.Functor
import com.github.fsbarata.functional.control.test.ComonadLaws
import com.github.fsbarata.functional.control.test.MonadLaws
import com.github.fsbarata.functional.control.test.MonadZipLaws
import com.github.fsbarata.functional.data.test.TraversableLaws

class TreeSequenceTest:
	MonadLaws<TreeSequenceContext>,
	MonadZipLaws<TreeSequenceContext>,
	ComonadLaws<TreeSequenceContext>,
	TraversableLaws<TreeSequenceContext> {
	override val monadScope = TreeSequence
	override val traversableScope = TreeSequence

	override fun <A> createFunctor(a: A) = TreeSequence(a)

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
