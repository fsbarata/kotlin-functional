package com.github.fsbarata.functional.data.list

import com.github.fsbarata.functional.control.Applicative
import com.github.fsbarata.functional.data.F1
import com.github.fsbarata.functional.data.F2
import com.github.fsbarata.functional.data.flip
import com.github.fsbarata.functional.data.partial

/**
 * Extensions to kotlin List, without needing to wrap in ListF
 *
 * Tested by ListFTest
 */

@Suppress("NOTHING_TO_INLINE")
inline fun <A, B> List<A>.ap(fs: List<(A) -> B>): List<B> =
	flatMap { a -> fs.map { f -> f(a) } }

inline fun <A, B, C> List<A>.liftA2(f: (A) -> (B) -> C): (List<B>) -> List<C> =
	{ lb -> flatMap { a -> lb.map(f(a)) } }

inline fun <F, A, B> List<A>.traverse(
	appScope: Applicative.Scope<F>,
	f: (A) -> Applicative<F, B>,
): Applicative<F, List<B>> {
	val plus: List<B>.(B) -> List<B> = List<B>::plus
	return fold(appScope.just(emptyList())) { app, a ->
		f(a).liftA2(plus.flip()::partial)(app)
	}
}
