package com.github.fsbarata.functional.data.sequence

import com.github.fsbarata.functional.control.Applicative
import com.github.fsbarata.functional.data.*

/**
 * Extensions to kotlin Sequence
 */

fun <A: Semigroup<A>> Sequence<A>.foldL(monoid: Monoid<A>) = fold(monoid.empty) { r, a -> r.combineWith(a) }

@Suppress("NOTHING_TO_INLINE")
inline fun <A, B> Sequence<A>.ap(fs: Sequence<(A) -> B>): Sequence<B> =
	flatMap { a -> fs.map { f -> f(a) } }

fun <A, B, C> Sequence<A>.lift2(lb: Sequence<B>, f: (A, B) -> C): Sequence<C> =
	flatMap { a -> lb.map(f.partial(a)) }

fun <A, M> Sequence<A>.foldMap(monoid: Monoid<M>, f: (A) -> M): M =
	fold(monoid.empty) { r, a -> monoid.combine(r, f(a)) }

fun <F, A, B> Sequence<A>.traverse(
	appScope: Applicative.Scope<F>,
	f: (A) -> Applicative<F, B>,
): Applicative<F, Sequence<B>> {
	return fold(appScope.just(emptySequence())) { app, a ->
		f(a).lift2(app) { b, lb -> lb + b }
	}
}
