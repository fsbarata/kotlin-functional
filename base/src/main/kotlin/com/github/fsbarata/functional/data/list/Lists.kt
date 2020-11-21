package com.github.fsbarata.functional.data.list

import com.github.fsbarata.functional.control.Applicative
import com.github.fsbarata.functional.data.Monoid
import com.github.fsbarata.functional.data.Semigroup
import com.github.fsbarata.functional.data.partial

/**
 * Extensions to kotlin List, without needing to wrap in ListF
 *
 * Tested by ListFTest
 */

fun <A: Semigroup<A>> List<A>.foldL(monoid: Monoid<A>) = fold(monoid.empty) { r, a -> r.combineWith(a) }

fun <A: Semigroup<A>> List<A>.foldR(monoid: Monoid<A>) = foldRight(monoid.empty) { r, a -> r.combineWith(a) }

@Suppress("NOTHING_TO_INLINE")
inline fun <A, B> List<A>.ap(fs: List<(A) -> B>): List<B> =
	fs.flatMap(this::map)

inline fun <A, B, C> List<A>.lift2(lb: List<B>, f: (A, B) -> C): List<C> =
	flatMap { a -> lb.map(f.partial(a)) }

inline fun <A, M> List<A>.foldMap(monoid: Monoid<M>, f: (A) -> M): M =
	fold(monoid.empty) { r, a -> monoid.combine(r, f(a)) }

inline fun <F, A, B> List<A>.traverse(
	appScope: Applicative.Scope<F>,
	f: (A) -> Applicative<F, B>,
): Applicative<F, List<B>> {
	return fold(appScope.just(emptyList())) { app, a ->
		f(a).lift2(app) { b, lb -> lb + b }
	}
}
