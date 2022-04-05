package com.github.fsbarata.functional.data.list

import com.github.fsbarata.functional.control.Applicative
import com.github.fsbarata.functional.data.*
import com.github.fsbarata.functional.data.maybe.Optional

/**
 * Extensions to kotlin List, without needing to wrap in ListF
 *
 * Tested by ListFTest
 */

fun <A> Iterable<A>.fold(monoid: Monoid<A>) = foldMap(monoid, id())
inline fun <A, M> Iterable<A>.foldMap(monoid: Monoid<M>, f: (A) -> M): M =
	fold(monoid.empty) { r, a -> monoid.combine(r, f(a)) }

inline fun <A> Iterable<A>.asFoldable() = object: Foldable<A> {
	override fun <R> foldL(initialValue: R, accumulator: (R, A) -> R) =
		this@asFoldable.fold(initialValue, accumulator)

	override fun <M> foldMap(monoid: Monoid<M>, f: (A) -> M) =
		this@asFoldable.foldMap(monoid, f)
}

fun <A: Semigroup<A>> Iterable<A>.foldL(initialValue: A): A = fold(initialValue, ::combine)
fun <A: Semigroup<A>> List<A>.foldR(initialValue: A): A = foldRight(initialValue, ::combine)
fun <A: Semigroup<A>> Iterable<A>.foldR(initialValue: A): A = asFoldable().foldR(initialValue, ::combine)

@Suppress("NOTHING_TO_INLINE")
inline fun <A, B> List<A>.ap(fs: List<(A) -> B>): List<B> =
	fs.flatMap(this::map)

inline fun <A, B, C> List<A>.lift2(lb: List<B>, f: (A, B) -> C): List<C> =
	flatMap { a -> lb.map(f.partial(a)) }

inline fun <F, A, B> List<A>.traverse(
	appScope: Applicative.Scope<F>,
	f: (A) -> Functor<F, B>,
): Functor<F, List<B>> {
	return fold(appScope.just(emptyList())) { app, a ->
		appScope.lift2(f(a), app) { b, lb -> lb + b }
	}
}

inline fun <A, R: Any> Iterable<A>.mapNotNone(f: (A) -> Optional<R>) =
	mapNotNull { f(it).orNull() }
