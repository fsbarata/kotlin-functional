package com.github.fsbarata.functional.control

import com.github.fsbarata.functional.Context
import com.github.fsbarata.functional.control.arrow.Kleisli
import com.github.fsbarata.functional.control.arrow.kleisli
import com.github.fsbarata.functional.data.compose
import com.github.fsbarata.functional.data.id
import com.github.fsbarata.functional.data.list.NonEmptyList
import com.github.fsbarata.functional.data.list.startWithItem
import com.github.fsbarata.functional.data.maybe.Optional

interface MonadPlus<M, out A>: Monad<M, A>, Alternative<M, A> {
	override val scope: Scope<M>

	override fun <B, R> lift2(fb: Context<M, B>, f: (A, B) -> R): MonadPlus<M, R> =
		super<Monad>.lift2(fb, f) as MonadPlus<M, R>

	override infix fun <B> bind(f: (A) -> Context<M, B>): MonadPlus<M, B>

	override fun combineWith(other: Context<M, @UnsafeVariance A>): MonadPlus<M, A>

	fun filter(predicate: (A) -> Boolean): MonadPlus<M, A> =
		scope.filter(this, predicate) as MonadPlus<M, A>

	fun partition(predicate: (A) -> Boolean): Pair<MonadPlus<M, A>, MonadPlus<M, A>> =
		Pair(
			filter(predicate),
			filter(Boolean::not compose predicate)
		)

	fun <B: Any> mapNotNull(f: (A) -> B?): MonadPlus<M, B> =
		scope.mapNotNull(this, f) as MonadPlus<M, B>

	fun <B: Any> mapNotNone(f: (A) -> Optional<B>): MonadPlus<M, B> =
		mapNotNull { f(it).orNull() }

	override fun some(): MonadPlus<M, NonEmptyList<A>> =
		bind { item -> many().map { list -> list.startWithItem(item) } }

	interface Scope<M>: Monad.Scope<M>, Alternative.Scope<M> {
		fun <A> filter(ca: Context<M, A>, predicate: (A) -> Boolean): Context<M, A> =
			bind(ca, filterKleisli(predicate))

		fun <A, B: Any> mapNotNull(ca: Context<M, A>, f: (A) -> B?): Context<M, B> =
			bind(ca, mapNotNullKleisli(f))

		override fun <A> some(ca: Context<M, A>): Context<M, NonEmptyList<A>> =
			if (ca is Alternative<M, A>) ca.some()
			else bind(ca) { item -> map(many(ca)) { list -> list.startWithItem(item) } }
	}
}

fun <M, A: Any> MonadPlus<M, A?>.filterNotNull() = mapNotNull(::id)
fun <M, A: Any> MonadPlus<M, Optional<A>>.filterNotNone() = mapNotNone(::id)


fun <M, A> MonadPlus.Scope<M>.filterKleisli(predicate: (A) -> Boolean): Kleisli<M, A, A> =
	kleisli { a: A -> if (predicate(a)) just(a) else empty() }

fun <M, A, B: Any> MonadPlus.Scope<M>.mapNotNullKleisli(f: (A) -> B?): Kleisli<M, A, B> =
	kleisli { a -> just(f(a) ?: return@kleisli empty()) }

fun <M, A, B: Any> MonadPlus.Scope<M>.mapNotNoneKleisli(f: (A) -> Optional<B>): Kleisli<M, A, B> =
	mapNotNullKleisli { a -> f(a).orNull() }

fun <M, A: Any> MonadPlus.Scope<M>.filterNotNullKleisli(): Kleisli<M, A?, A> = mapNotNullKleisli(::id)
fun <M, A: Any> MonadPlus.Scope<M>.filterNotNoneKleisli(): Kleisli<M, Optional<A>, A> = mapNotNoneKleisli(::id)
