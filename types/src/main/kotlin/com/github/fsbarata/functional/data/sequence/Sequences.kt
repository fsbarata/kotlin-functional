package com.github.fsbarata.functional.data.sequence

import com.github.fsbarata.functional.data.*

/**
 * Extensions to kotlin Sequence
 */

@Suppress("NOTHING_TO_INLINE")
inline fun <A, B> Sequence<A>.ap(fs: Sequence<(A) -> B>): Sequence<B> =
	flatMap { a -> fs.map { f -> f(a) } }

fun <A, B, C> Sequence<A>.lift2(lb: Sequence<B>, f: (A, B) -> C): Sequence<C> =
	flatMap { a -> lb.map(f.partial(a)) }

fun <M, A> Sequence<A>.foldMap(monoid: Monoid<M>, f: (A) -> M): M =
	fold(monoid.empty, (monoid.dual()::combine compose f).flip())