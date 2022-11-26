package com.github.fsbarata.functional.data

import com.github.fsbarata.functional.Context
import com.github.fsbarata.functional.control.Applicative

interface Traversable<T, out A>: Functor<T, A>, Foldable<A> {
	val scope: Scope<T>

	override fun <B> map(f: (A) -> B): Traversable<T, B>

	fun <F, B> traverse(
		appScope: Applicative.Scope<F>,
		f: (A) -> Context<F, B>,
	): Context<F, Traversable<T, B>> =
		@Suppress("UNCHECKED_CAST")
		traverseFromSequence(scope, this, appScope, f) as Context<F, Traversable<T, B>>

	interface Scope<T>: Functor.Scope<T> {
		fun <F, A> sequenceA(
			appScope: Applicative.Scope<F>,
			t: Context<T, Context<F, A>>,
		): Context<F, Context<T, A>> =
			sequenceFromTraverse(this, t, appScope)

		fun <F, A, B> traverse(
			ca: Context<T, A>,
			appScope: Applicative.Scope<F>,
			f: (A) -> Context<F, B>,
		): Context<F, Context<T, B>> =
			(ca as Traversable<T, A>).traverse(appScope, f)
	}
}

fun <T, F, A, B> traverseFromSequence(
	scope: Traversable.Scope<T>,
	t: Context<T, A>,
	appScope: Applicative.Scope<F>,
	f: (A) -> Context<F, B>,
): Context<F, Context<T, B>> =
	scope.sequenceA(appScope, scope.map(t, f))


fun <T, F, A> sequenceFromTraverse(
	scope: Traversable.Scope<T>,
	t: Context<T, Context<F, A>>,
	appScope: Applicative.Scope<F>,
): Context<F, Context<T, A>> =
	scope.traverse(t, appScope, id())

