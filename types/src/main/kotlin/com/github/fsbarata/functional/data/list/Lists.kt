package com.github.fsbarata.functional.data.list

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

