package com.github.fsbarata.functional.data.compose

import com.github.fsbarata.functional.control.Applicative
import com.github.fsbarata.functional.data.Traversable

class CompositeTraversable<F, G, A>(
	override val fg: Traversable<F, Traversable<G, A>>,
): Traversable<Composite<F, G, *>, A>,
	Composite<F, G, A>(fg) {
	override val scope = Scope<F, G>()

	override fun <B> map(f: (A) -> B): CompositeTraversable<F, G, B> =
		fg.map { g -> g.map(f) }.composite()

	override fun <R> foldL(initialValue: R, accumulator: (R, A) -> R): R =
		fg.foldL(initialValue) { acc, ga -> ga.foldL(acc, accumulator) }

	override fun <R> foldR(initialValue: R, accumulator: (A, R) -> R): R =
		fg.foldR(initialValue) { ga, acc -> ga.foldR(acc, accumulator) }

	override fun <H, B> traverse(
		appScope: Applicative.Scope<H>,
		f: (A) -> Applicative<H, B>,
	): Applicative<H, CompositeTraversable<F, G, B>> =
		fg.traverse(appScope) { it.traverse(appScope, f) }
			.map { CompositeTraversable(it) }

	class Scope<F, G>: Traversable.Scope<Composite<F, G, *>>
}

fun <F, G, A> Traversable<F, Traversable<G, A>>.composite() =
	CompositeTraversable(this)

fun <F, G, A> Traversable<Composite<F, G, *>, A>.asCompose() = this as CompositeTraversable<F, G, A>
