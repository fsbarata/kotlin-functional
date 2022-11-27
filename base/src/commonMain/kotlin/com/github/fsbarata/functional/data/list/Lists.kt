package com.github.fsbarata.functional.data.list

import com.github.fsbarata.functional.Context
import com.github.fsbarata.functional.control.Applicative
import com.github.fsbarata.functional.data.Semigroup
import com.github.fsbarata.functional.data.concat

/**
 * Extensions to kotlin List, without needing to wrap in ListF
 *
 * Tested by ListFTest
 */

fun <A: Semigroup<A>> List<A>.foldR(initialValue: A): A = foldRight(initialValue, ::concat)

@Suppress("NOTHING_TO_INLINE")
inline fun <A, B> List<A>.ap(fs: List<(A) -> B>): List<B> =
	fs.flatMap(::map)

inline fun <A, B, C> List<A>.lift2(lb: List<B>, f: (A, B) -> C): List<C> =
	flatMap { a -> lb.map { b -> f(a, b) } }

inline fun <F, A, B> Iterable<A>.traverse(
	appScope: Applicative.Scope<F>,
	f: (A) -> Context<F, B>,
): Context<F, ListF<B>> {
	return fold(appScope.just(ListF.empty())) { app, a ->
		appScope.lift2(f(a), app) { b, lb -> lb + b }
	}
}

