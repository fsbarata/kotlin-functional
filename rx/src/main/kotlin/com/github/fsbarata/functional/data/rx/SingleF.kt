package com.github.fsbarata.functional.data.rx

import com.github.fsbarata.functional.Context
import com.github.fsbarata.functional.control.Lift2
import com.github.fsbarata.functional.control.Lift3
import com.github.fsbarata.functional.control.Lift4
import com.github.fsbarata.functional.control.MonadZip
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.core.SingleObserver

class SingleF<A>(private val wrapped: Single<A>): Single<A>(),
	MonadZip<SingleContext, A> {
	override val scope get() = SingleF

	override fun subscribeActual(observer: SingleObserver<in A>) {
		wrapped.subscribe(observer)
	}

	override fun <B> map(f: (A) -> B) = wrapped.map(f).f()

	override infix fun <B> bind(f: (A) -> Context<SingleContext, B>): SingleF<B> =
		flatMap { f(it).asSingle }

	fun <B> flatMap(f: (A) -> Single<B>): SingleF<B> =
		wrapped.flatMap(f).f()

	override fun <B, R> zipWith(other: Context<SingleContext, B>, f: (A, B) -> R) =
		(this as Single<A>).zipWith(other.asSingle, f).f()

	companion object: MonadZip.Scope<SingleContext> {
		override fun <A> just(a: A) = Single.just(a).f()
	}
}

internal typealias SingleContext = Single<*>

fun <A> Single<A>.f() = SingleF(this)

val <A> Context<SingleContext, A>.asSingle
	get() = this as SingleF<A>

operator fun <A, B, R> Lift2<A, B, R>.invoke(
	single1: SingleF<A>,
	single2: SingleF<B>,
): SingleF<R> = app(single1, single2).asSingle

operator fun <A, B, C, R> Lift3<A, B, C, R>.invoke(
	single1: SingleF<A>,
	single2: SingleF<B>,
	single3: SingleF<C>,
): SingleF<R> = app(single1, single2, single3).asSingle

operator fun <A, B, C, D, R> Lift4<A, B, C, D, R>.invoke(
	single1: SingleF<A>,
	single2: SingleF<B>,
	single3: SingleF<C>,
	single4: SingleF<D>,
): SingleF<R> = app(single1, single2, single3, single4).asSingle
