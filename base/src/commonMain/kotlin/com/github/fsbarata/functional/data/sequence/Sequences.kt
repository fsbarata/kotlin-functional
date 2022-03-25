package com.github.fsbarata.functional.data.sequence

import com.github.fsbarata.functional.control.Applicative
import com.github.fsbarata.functional.data.Functor
import com.github.fsbarata.functional.data.Monoid
import com.github.fsbarata.functional.data.Semigroup
import com.github.fsbarata.functional.data.partial

/**
 * Extensions to kotlin Sequence
 */

fun <A: Semigroup<A>> Sequence<A>.foldL(monoid: Monoid<A>) = fold(monoid.empty) { r, a -> r.combineWith(a) }

fun <A, B> Sequence<A>.ap(fs: Sequence<(A) -> B>): Sequence<B> =
	fs.flatMap(this::map)

fun <A, B, C> Sequence<A>.lift2(lb: Sequence<B>, f: (A, B) -> C): Sequence<C> =
	flatMap { a -> lb.map(partial(f, a)) }

inline fun <A, M> Sequence<A>.foldMap(monoid: Monoid<M>, f: (A) -> M): M =
	fold(monoid.empty) { r, a -> monoid.combine(r, f(a)) }

inline fun <F, A, B> Sequence<A>.traverse(
	appScope: Applicative.Scope<F>,
	f: (A) -> Functor<F, B>,
): Functor<F, Sequence<B>> {
	return fold(appScope.just(emptySequence())) { app, a ->
		appScope.lift2(f(a), app) { b, lb -> lb + b }
	}
}
