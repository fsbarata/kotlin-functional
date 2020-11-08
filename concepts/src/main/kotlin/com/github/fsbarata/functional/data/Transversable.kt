package com.github.fsbarata.functional.data

import com.github.fsbarata.functional.control.Applicative
import com.github.fsbarata.functional.control.Functor

interface Traversable<T, out A>: Functor<T, A>, Foldable<A> {
	val scope: Scope<T>

	override fun <B> map(f: (A) -> B): Traversable<T, B>

	fun <F, B> traverse(
		appScope: Applicative.Scope<F>,
		f: (A) -> Applicative<F, B>,
	): Applicative<F, Traversable<T, B>> =
		traverseFromSequence(appScope, this, f)

	interface Scope<T> {
		fun <F, A> sequenceA(
			appScope: Applicative.Scope<F>,
			t: Traversable<T, Applicative<F, A>>,
		): Applicative<F, Traversable<T, A>> =
			sequenceFromTraverse(appScope, t)
	}
}

fun <T, F, A, B> traverseFromSequence(
		appScope: Applicative.Scope<F>,
		t: Traversable<T, A>,
		f: (A) -> Applicative<F, B>,
): Applicative<F, Traversable<T, B>> =
		t.scope.sequenceA(appScope, t.map(f) as Traversable<T, Applicative<F, B>>)


fun <T, F, A> sequenceFromTraverse(
		appScope: Applicative.Scope<F>,
		t: Traversable<T, Applicative<F, A>>,
): Applicative<F, Traversable<T, A>> =
		t.traverse(appScope, id())

