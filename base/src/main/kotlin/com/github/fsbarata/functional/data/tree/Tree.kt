package com.github.fsbarata.functional.data.tree

import com.github.fsbarata.functional.Context
import com.github.fsbarata.functional.control.Applicative
import com.github.fsbarata.functional.control.Comonad
import com.github.fsbarata.functional.control.Monad
import com.github.fsbarata.functional.control.MonadZip
import com.github.fsbarata.functional.data.Monoid
import com.github.fsbarata.functional.data.Traversable
import com.github.fsbarata.functional.data.list.NonEmptyIterable
import com.github.fsbarata.functional.data.list.f
import com.github.fsbarata.functional.data.list.traverse
import com.github.fsbarata.functional.data.partial
import java.io.Serializable

typealias Forest<A> = List<Tree<A>>

internal typealias TreeContext = Tree<*>

@Suppress("OVERRIDE_BY_INLINE")
data class Tree<out A>(val root: A, val sub: Forest<A> = emptyList()): NonEmptyIterable<A>,
	MonadZip<TreeContext, A>,
	Traversable<TreeContext, A>,
	Comonad<TreeContext, A>,
	Serializable {
	override val scope = Tree

	override val head: A = root
	override val tail: Iterable<A> = Iterable { sub.asSequence().flatten().iterator() }

	override fun extract() = root

	override infix fun <B> bind(f: (A) -> Context<TreeContext, B>) = flatMap { f(it).asTree }

	fun <B> flatMap(f: (A) -> Tree<B>): Tree<B> {
		val newTs = f(root)
		return Tree(
			newTs.root,
			newTs.sub + sub.map { t -> t.flatMap(f) }
		)
	}

	override fun <B> ap(ff: Applicative<TreeContext, (A) -> B>): Tree<B> {
		val (f, tfs) = ff.asTree
		return Tree(
			f(root),
			sub.map { ta -> ta.map(f) } + tfs.map(this::ap)
		)
	}

	override fun <B, R> lift2(fb: Applicative<TreeContext, B>, f: (A, B) -> R): Tree<R> {
		val tb = fb.asTree
		val fRoot = f.partial(root)
		return Tree(
			f(root, tb.root),
			tb.sub.map { it.map(fRoot) } + sub.map { it.lift2(tb, f) }
		)
	}

	override fun <B, R> zipWith(other: MonadZip<TreeContext, B>, f: (A, B) -> R): Tree<R> {
		val otherTree = other.asTree
		return Tree(
			f(root, otherTree.root),
			sub.zip(otherTree.sub) { ta, tb -> ta.zipWith(tb, f) }
		)
	}

	override fun <B> map(f: (A) -> B): Tree<B> =
		Tree(f(root), sub.map { it.map(f) })

	override fun <M> foldMap(monoid: Monoid<M>, f: (A) -> M): M =
		monoid.combine(f(root), sub.f().foldMap(monoid) { ta -> ta.foldMap(monoid, f) })

	override fun <F, B> traverse(
		appScope: Applicative.Scope<F>,
		f: (A) -> Applicative<F, B>,
	): Applicative<F, Tree<B>> =
		f(root).lift2(sub.traverse(appScope) { it.traverse(appScope, f) }, ::Tree)

	override fun duplicate(): Tree<Tree<A>> =
		Tree(this, sub.map { it.duplicate() })

	fun toTreeSequence(): TreeSequence<A> = TreeSequence(
		root,
		sub.asSequence().map { it.toTreeSequence() }
	)

	companion object:
		Monad.Scope<TreeContext>,
		Traversable.Scope<TreeContext> {
		override fun <A> just(a: A) = Tree(a)
	}
}

val <A> Context<TreeContext, A>.asTree
	get() = this as Tree<A>