package com.github.fsbarata.functional.data.rx

import com.github.fsbarata.functional.Context
import com.github.fsbarata.functional.control.*
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.core.SingleObserver

class SingleF<A: Any>(private val wrapped: Single<A>): Single<A>(),
	MonadZip<SingleContext, A> {
	override val scope get() = SingleF

	override fun subscribeActual(observer: SingleObserver<in A>) {
		wrapped.subscribe(observer)
	}

	override fun <B> map(f: (A) -> B) = wrapped.map { f(it)!! }.f()

	override fun <B, R> lift2(fb: Context<SingleContext, B>, f: (A, B) -> R): SingleF<R & Any> {
		return zipWith(fb, f)
	}

	override fun <B> ap(ff: Context<SingleContext, (A) -> B>): SingleF<B & Any> {
		return apFromLift2(SingleF, this, ff).asSingle
	}

	override infix fun <B> bind(f: (A) -> Context<SingleContext, B>): SingleF<B & Any> =
		flatMap { f(it) as Single<B & Any> }

	fun <B: Any> flatMap(f: (A) -> Single<B>): SingleF<B> =
		SingleF(wrapped.flatMap(f))

	override fun <B, R> zipWith(other: Context<SingleContext, B>, f: (A, B) -> R): SingleF<R & Any> {
		return SingleF(wrapped.zipWith(other as Single<B & Any>) { a, b -> f(a, b)!! })
	}

	companion object: MonadZip.Scope<SingleContext> {
		override fun <A> just(a: A): SingleF<A & Any> = SingleF(Single.just(a!!))
	}
}

internal typealias SingleContext = Single<*>

fun <A: Any> Single<A>.f() = SingleF(this)

val <A> Context<SingleContext, A>.asSingle
	get() = this as SingleF<A & Any>

operator fun <A: Any, B: Any, R: Any> Lift2<A, B, R>.invoke(
	single1: SingleF<A>,
	single2: SingleF<B>,
): SingleF<R> = app(single1, single2).asSingle

operator fun <A: Any, B: Any, C: Any, R: Any> Lift3<A, B, C, R>.invoke(
	single1: SingleF<A>,
	single2: SingleF<B>,
	single3: SingleF<C>,
): SingleF<R> = app(single1, single2, single3).asSingle

operator fun <A: Any, B: Any, C: Any, D: Any, R: Any> Lift4<A, B, C, D, R>.invoke(
	single1: SingleF<A>,
	single2: SingleF<B>,
	single3: SingleF<C>,
	single4: SingleF<D>,
): SingleF<R> = app(single1, single2, single3, single4).asSingle
