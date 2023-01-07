package com.github.fsbarata.functional.data.rx

import com.github.fsbarata.functional.Context
import com.github.fsbarata.functional.control.Lift2
import com.github.fsbarata.functional.control.Lift3
import com.github.fsbarata.functional.control.Lift4
import com.github.fsbarata.functional.control.MonadZip
import com.github.fsbarata.functional.data.id
import com.github.fsbarata.functional.data.maybe.Optional
import com.github.fsbarata.functional.data.maybe.toOptional
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.core.MaybeObserver

class MaybeF<A>(private val wrapped: Maybe<A>): Maybe<A>(),
	MonadZip<MaybeContext, A> {
	override val scope get() = MaybeF

	override fun subscribeActual(observer: MaybeObserver<in A>) {
		wrapped.subscribe(observer)
	}

	override fun <B> map(f: (A) -> B) =
		wrapped.map(f).f()

	override infix fun <B> bind(f: (A) -> Context<MaybeContext, B>): MaybeF<B> =
		flatMap { f(it).asMaybe }

	fun <B> flatMap(f: (A) -> Maybe<B>): MaybeF<B> =
		wrapped.flatMap(f).f()

	override fun <B, R> zipWith(other: Context<MaybeContext, B>, f: (A, B) -> R) =
		(this as Maybe<A>).zipWith(other.asMaybe, f).f()

	companion object: MonadZip.Scope<MaybeContext> {
		fun <A> empty() = Maybe.empty<A>().f()
		override fun <A> just(a: A) = Maybe.just(a).f()
	}
}

fun <A: Any, R: Any> Maybe<A>.mapNotNull(f: (A) -> R?): Maybe<R> =
	mapNotNone { f(it).toOptional() }

fun <A: Any, R: Any> Maybe<A>.mapNotNone(f: (A) -> Optional<R>): Maybe<R> =
	map(f).filter { it.isPresent() }
		.map { it.orNull()!! }

fun <A: Any> Maybe<Optional<A>>.filterNotNone(): Maybe<A> =
	mapNotNone(id())

internal typealias MaybeContext = Maybe<*>

fun <A> Maybe<A>.f() = MaybeF(this)

val <A> Context<MaybeContext, A>.asMaybe
	get() = this as MaybeF<A>

operator fun <A: Any, B: Any, R: Any> Lift2<A, B, R>.invoke(
	maybe1: MaybeF<A>,
	maybe2: MaybeF<B>,
): MaybeF<R> = app(maybe1, maybe2).asMaybe

operator fun <A, B, C, R> Lift3<A, B, C, R>.invoke(
	maybe1: MaybeF<A>,
	maybe2: MaybeF<B>,
	maybe3: MaybeF<C>,
): MaybeF<R> = app(maybe1, maybe2, maybe3).asMaybe

operator fun <A, B, C, D, R> Lift4<A, B, C, D, R>.invoke(
	maybe1: MaybeF<A>,
	maybe2: MaybeF<B>,
	maybe3: MaybeF<C>,
	maybe4: MaybeF<D>,
): MaybeF<R> = app(maybe1, maybe2, maybe3, maybe4).asMaybe
