package com.github.fsbarata.functional.data.compose

import com.github.fsbarata.functional.control.Applicative
import com.github.fsbarata.functional.data.Traversable

class ComposedTraversable<F, G, A>(
	override val fg: Traversable<F, Traversable<G, A>>,
): Traversable<Composed<F, G, *>, A>,
	Composed<F, G, A>(fg) {
	override val scope = Scope<F, G>()

	override fun <B> map(f: (A) -> B): ComposedTraversable<F, G, B> =
		fg.map { g -> g.map(f) }.compose()

	override fun <R> foldL(initialValue: R, accumulator: (R, A) -> R): R =
		fg.foldL(initialValue) { acc, ga -> ga.foldL(acc, accumulator) }

	override fun <R> foldR(initialValue: R, accumulator: (A, R) -> R): R =
		fg.foldR(initialValue) { ga, acc -> ga.foldR(acc, accumulator) }

	override fun <H, B> traverse(
		appScope: Applicative.Scope<H>,
		f: (A) -> Applicative<H, B>,
	): Applicative<H, ComposedTraversable<F, G, B>> =
		fg.traverse(appScope) { it.traverse(appScope, f) }
			.map { ComposedTraversable(it) }

	class Scope<F, G>: Traversable.Scope<Composed<F, G, *>>
}

fun <F, G, A> Traversable<F, Traversable<G, A>>.compose() =
	ComposedTraversable(this)

val <F, G, A> Traversable<Composed<F, G, *>, A>.asCompose
	get() = this as ComposedTraversable<F, G, A>
