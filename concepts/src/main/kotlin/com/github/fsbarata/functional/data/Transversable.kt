package com.github.fsbarata.functional.data

import com.github.fsbarata.functional.control.Applicative
import com.github.fsbarata.functional.control.Functor

interface Traversable<C, out A>: Functor<C, A>, Foldable<A> {
	val scope: Scope<C>

	fun <F, B> traverse(
		appScope: Applicative.Scope<F>,
		f: (A) -> Applicative<F, B>,
	): Applicative<F, Traversable<C, B>> =
		traverseFromSequence(appScope, this, f)

	interface Scope<C> {
		fun <F, A> sequenceA(
			appScope: Applicative.Scope<F>,
			t: Traversable<C, Applicative<F, A>>,
		): Applicative<F, Traversable<C, A>> =
			sequenceFromTraverse(appScope, t)
	}
}

fun <C, F, A, B> traverseFromSequence(
		appScope: Applicative.Scope<F>,
		t: Traversable<C, A>,
		f: (A) -> Applicative<F, B>,
): Applicative<F, Traversable<C, B>> =
		t.scope.sequenceA(appScope, t.map(f) as Traversable<C, Applicative<F, B>>)


fun <C, F, A> sequenceFromTraverse(
		appScope: Applicative.Scope<F>,
		t: Traversable<C, Applicative<F, A>>,
): Applicative<F, Traversable<C, A>> =
		t.traverse(appScope, id())

