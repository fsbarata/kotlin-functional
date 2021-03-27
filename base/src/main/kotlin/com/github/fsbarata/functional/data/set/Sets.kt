package com.github.fsbarata.functional.data.set

import com.github.fsbarata.functional.control.Applicative
import com.github.fsbarata.functional.data.Monoid
import com.github.fsbarata.functional.data.partial

/**
 * Extensions to kotlin Set, without needing to wrap in SetF
 *
 * Tested by SetFTest
 */

@Suppress("NOTHING_TO_INLINE")
inline fun <A, B> Set<A>.ap(fs: Set<(A) -> B>): Set<B> =
	fs.flatMapTo(mutableSetOf(), this::map)

inline fun <A, B, C> Set<A>.lift2(lb: Set<B>, f: (A, B) -> C): Set<C> =
	flatMapTo(mutableSetOf()) { a -> lb.map(f.partial(a)) }

inline fun <A, M> Set<A>.foldMap(monoid: Monoid<M>, f: (A) -> M): M =
	fold(monoid.empty) { r, a -> monoid.combine(r, f(a)) }

inline fun <F, A, B> Set<A>.traverse(
	appScope: Applicative.Scope<F>,
	f: (A) -> Applicative<F, B>,
): Applicative<F, Set<B>> {
	return fold(appScope.just(emptySet())) { app, a ->
		f(a).lift2(app) { b, lb -> lb + b }
	}
}