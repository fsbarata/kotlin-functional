package com.github.fsbarata.functional.data.tree

import com.github.fsbarata.functional.control.Applicative
import com.github.fsbarata.functional.control.Comonad
import com.github.fsbarata.functional.control.Context
import com.github.fsbarata.functional.control.Monad
import com.github.fsbarata.functional.data.Monoid
import com.github.fsbarata.functional.data.Traversable
import com.github.fsbarata.functional.data.partial
import com.github.fsbarata.functional.data.sequence.NonEmptySequenceBase
import com.github.fsbarata.functional.data.sequence.foldMap
import com.github.fsbarata.functional.data.sequence.traverse
import com.github.fsbarata.functional.iterators.NonEmptyIterator

typealias ForestSequence<A> = Sequence<TreeSequence<A>>

internal typealias TreeSequenceContext = TreeSequence<*>

@Suppress("OVERRIDE_BY_INLINE")
class TreeSequence<out A>(
	val root: A,
	val sub: ForestSequence<A> = emptySequence(),
): Monad<TreeSequenceContext, A>,
	Comonad<TreeSequenceContext, A>,
	Traversable<TreeSequenceContext, A>,
	NonEmptySequenceBase<A> {
	override val scope = TreeSequence

	override fun iterator() = NonEmptyIterator(
		root,
		sub.flatten().iterator()
	)

	override fun extract() = root

	override fun <B> bind(f: (A) -> Context<TreeSequenceContext, B>) = flatMap { f(it).asTreeSequence }

	fun <B> flatMap(f: (A) -> TreeSequence<B>): TreeSequence<B> {
		val newTree = f(root)
		return TreeSequence(
			newTree.root,
			newTree.sub + sub.flatten().map(f)
		)
	}

	override fun <B, R> lift2(fb: Applicative<TreeSequenceContext, B>, f: (A, B) -> R): TreeSequence<R> {
		val tb = fb.asTreeSequence
		val x: ForestSequence<R> = tb.sub.map { it.map(f.partial(root)) }
		val y: ForestSequence<R> = sub.map { it.lift2(tb, f) }
		return TreeSequence(
			f(root, tb.root),
			x + y
		)
	}

	override fun <B> map(f: (A) -> B): TreeSequence<B> =
		TreeSequence(f(root), sub.map { it.map(f) })

	override fun <M> foldMap(monoid: Monoid<M>, f: (A) -> M): M =
		monoid.combine(f(root), sub.foldMap(monoid) { ta -> ta.foldMap(monoid, f) })

	override fun duplicate(): TreeSequence<TreeSequence<A>> =
		TreeSequence(this, sub.map { it.duplicate() })

	override fun <F, B> traverse(
		appScope: Applicative.Scope<F>,
		f: (A) -> Applicative<F, B>,
	): Applicative<F, TreeSequence<B>> =
		f(root).lift2(sub.traverse(appScope) { it.traverse(appScope, f) }, ::TreeSequence)

	fun toTree(): Tree<A> = Tree(
		root,
		sub.map { it.toTree() }.toList()
	)

	companion object
		: Monad.Scope<TreeSequenceContext>,
		Traversable.Scope<TreeSequenceContext> {
		override fun <A> just(a: A) = TreeSequence(a)
	}
}

val <A> Context<TreeSequenceContext, A>.asTreeSequence
	get() = this as TreeSequence<A>