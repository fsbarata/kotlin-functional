package com.github.fsbarata.functional.data.tree

import com.github.fsbarata.functional.Context
import com.github.fsbarata.functional.control.Applicative
import com.github.fsbarata.functional.control.Comonad
import com.github.fsbarata.functional.control.Monad
import com.github.fsbarata.functional.control.MonadZip
import com.github.fsbarata.functional.data.Functor
import com.github.fsbarata.functional.data.Monoid
import com.github.fsbarata.functional.data.Traversable
import com.github.fsbarata.functional.data.partial
import com.github.fsbarata.functional.data.sequence.NonEmptySequenceBase
import com.github.fsbarata.functional.data.sequence.foldMap
import com.github.fsbarata.functional.data.sequence.traverse
import com.github.fsbarata.functional.utils.nonEmptyIterator

typealias Forest<A> = Sequence<Tree<A>>

internal typealias TreeContext = Tree<*>

@Suppress("OVERRIDE_BY_INLINE")
class Tree<out A>(
	val root: A,
	val sub: Forest<A> = emptySequence(),
):
	MonadZip<TreeContext, A>,
	Comonad<TreeContext, A>,
	Traversable<TreeContext, A>,
	NonEmptySequenceBase<A> {
	override val scope = Tree

	override fun iterator() = nonEmptyIterator(
		root,
		sub.flatten().iterator()
	)

	override fun extract() = root

	override infix fun <B> bind(f: (A) -> Context<TreeContext, B>) = flatMap { f(it).asTree }

	fun <B> flatMap(f: (A) -> Tree<B>): Tree<B> {
		val newTs = f(root)
		return Tree(
			newTs.root,
			newTs.sub + sub.map { t -> t.flatMap(f) }
		)
	}

	override fun <B> ap(ff: Functor<TreeContext, (A) -> B>): Tree<B> {
		val s = ff.asTree
		val f = s.root
		val tfs = s.sub
		return Tree(
			f(root),
			sub.map { ta -> ta.map(f) } + tfs.map(this::ap)
		)
	}

	override fun <B, R> lift2(fb: Functor<TreeContext, B>, f: (A, B) -> R): Tree<R> {
		val tb = fb.asTree
		val x: Forest<R> = tb.sub.map { it.map(f.partial(root)) }
		val y: Forest<R> = sub.map { it.lift2(tb, f) }
		return Tree(
			f(root, tb.root),
			x + y
		)
	}

	override fun <B> map(f: (A) -> B): Tree<B> =
		Tree(f(root), sub.map { it.map(f) })

	override fun <M> foldMap(monoid: Monoid<M>, f: (A) -> M): M =
		monoid.combine(f(root), sub.foldMap(monoid) { ta -> ta.foldMap(monoid, f) })

	override fun <B, R> zipWith(
		other: Functor<TreeContext, B>,
		f: (A, B) -> R,
	): Tree<R> {
		val otherTree = other.asTree
		return Tree(
			f(root, otherTree.root),
			sub.zip(otherTree.sub) { ta, tb -> ta.zipWith(tb, f) }
		)
	}

	override fun duplicate(): Tree<Tree<A>> =
		Tree(this, sub.map { it.duplicate() })

	override fun <F, B> traverse(
		appScope: Applicative.Scope<F>,
		f: (A) -> Functor<F, B>,
	): Functor<F, Tree<B>> =
		appScope.lift2(f(root), sub.traverse(appScope) { it.traverse(appScope, f) }, ::Tree)

	fun <F, B> traverse(
		f: (A) -> Applicative<F, B>,
	): Functor<F, Tree<B>> {
		val mappedRoot = f(root)
		return mappedRoot.lift2(sub.traverse(mappedRoot.scope) { it.traverse(f) }, ::Tree)
	}

	companion object:
		Monad.Scope<TreeContext>,
		Traversable.Scope<TreeContext> {
		override fun <A> just(a: A) = Tree(a)
	}
}

val <A> Context<TreeContext, A>.asTree
	get() = this as Tree<A>