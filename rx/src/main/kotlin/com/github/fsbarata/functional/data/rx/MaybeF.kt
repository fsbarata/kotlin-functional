package com.github.fsbarata.functional.data.rx

import com.github.fsbarata.functional.Context
import com.github.fsbarata.functional.control.*
import com.github.fsbarata.functional.data.id
import com.github.fsbarata.functional.data.maybe.Optional
import com.github.fsbarata.functional.data.maybe.toOptional
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.core.MaybeObserver

class MaybeF<A: Any>(private val wrapped: Maybe<A>): Maybe<A>(),
	MonadZip<MaybeContext, A>,
	MonadPlus<MaybeContext, A> {
	override val scope get() = MaybeF

	override fun subscribeActual(observer: MaybeObserver<in A>) {
		wrapped.subscribe(observer)
	}

	override fun <B> map(f: (A) -> B): MaybeF<B & Any> =
		wrapped.map { f(it)!! }.f()

	override fun <B, R> lift2(fb: Context<MaybeContext, B>, f: (A, B) -> R): MaybeF<R & Any> {
		return zipWith(fb, f)
	}

	override fun <B> ap(ff: Context<MaybeContext, (A) -> B>): MaybeF<B & Any> {
		return apFromLift2(MaybeF, this, ff).asMaybe
	}

	override infix fun <B> bind(f: (A) -> Context<MaybeContext, B>): MaybeF<B & Any> =
		wrapped.flatMap { f(it) as MaybeF<B & Any> }.f()

	fun <B: Any> flatMap(f: (A) -> Maybe<B>): MaybeF<B> =
		wrapped.flatMap(f).f()

	override fun <B, R> zipWith(other: Context<MaybeContext, B>, f: (A, B) -> R): MaybeF<R & Any> =
		(this as Maybe<A>).zipWith(other as Maybe<B & Any>) { a, b -> f(a, b)!! }.f()

	override fun combineWith(other: Context<MaybeContext, A>): MaybeF<A> {
		return MaybeF(wrapped.switchIfEmpty(other.asMaybe))
	}

	companion object: MonadZip.Scope<MaybeContext>, MonadPlus.Scope<MaybeContext> {
		override fun <A> empty(): MaybeF<A & Any> = MaybeF(Maybe.empty())
		override fun <A> just(a: A): MaybeF<A & Any> = MaybeF(Maybe.just(a!!))
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

fun <A: Any> Maybe<A>.f() = MaybeF(this)

val <A> Context<MaybeContext, A>.asMaybe
	get() = this as MaybeF<A & Any>

operator fun <A: Any, B: Any, R: Any> Lift2<A, B, R>.invoke(
	maybe1: MaybeF<A>,
	maybe2: MaybeF<B>,
): MaybeF<R> = app(maybe1, maybe2).asMaybe

operator fun <A: Any, B: Any, C: Any, R: Any> Lift3<A, B, C, R>.invoke(
	maybe1: MaybeF<A>,
	maybe2: MaybeF<B>,
	maybe3: MaybeF<C>,
): MaybeF<R> = app(maybe1, maybe2, maybe3).asMaybe

operator fun <A: Any, B: Any, C: Any, D: Any, R: Any> Lift4<A, B, C, D, R>.invoke(
	maybe1: MaybeF<A>,
	maybe2: MaybeF<B>,
	maybe3: MaybeF<C>,
	maybe4: MaybeF<D>,
): MaybeF<R> = app(maybe1, maybe2, maybe3, maybe4).asMaybe
