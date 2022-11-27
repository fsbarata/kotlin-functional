package com.github.fsbarata.functional.data.set

import com.github.fsbarata.functional.Context
import com.github.fsbarata.functional.control.Applicative
import com.github.fsbarata.functional.data.Monoid
import com.github.fsbarata.functional.data.maybe.Optional

/**
 * Extensions to kotlin Set, without needing to wrap in SetF
 *
 * Tested by SetFTest
 */

inline fun <A, B> Set<A>.ap(fs: Set<(A) -> B>): Set<B> =
	fs.flatMapToSet(this::map)

inline fun <A, B, C> Set<A>.lift2(lb: Set<B>, f: (A, B) -> C): Set<C> =
	flatMapToSet { a -> lb.map { b -> f(a, b) } }

inline fun <A, M> Set<A>.foldMap(monoid: Monoid<M>, f: (A) -> M): M =
	fold(monoid.empty) { r, a -> monoid.concat(r, f(a)) }

inline fun <A, B> Set<A>.flatMapToSet(f: (A) -> Iterable<B>): Set<B> =
	flatMapTo(mutableSetOf(), f)

inline fun <F, A, B> Set<A>.traverse(
	appScope: Applicative.Scope<F>,
	f: (A) -> Context<F, B>,
): Context<F, Set<B>> {
	return fold(appScope.just(emptySet())) { app, a ->
		appScope.lift2(f(a), app) { b, lb -> lb + b }
	}
}