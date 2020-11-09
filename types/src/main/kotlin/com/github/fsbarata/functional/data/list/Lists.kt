package com.github.fsbarata.functional.data.list

import com.github.fsbarata.functional.control.Applicative
import com.github.fsbarata.functional.control.liftAC2
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

inline fun <A, B, C> List<A>.lift2(lb: List<B>, f: (A, B) -> C): List<C> =
	flatMap { a -> lb.map(f.partial(a)) }

inline fun <F, A, B> List<A>.traverse(
	appScope: Applicative.Scope<F>,
	f: (A) -> Applicative<F, B>,
): Applicative<F, List<B>> {
	val plus: List<B>.(B) -> List<B> = List<B>::plus
	return fold(appScope.just(emptyList())) { app, a ->
		liftAC2(f(a), plus.flip()::partial)(app)
	}
}
