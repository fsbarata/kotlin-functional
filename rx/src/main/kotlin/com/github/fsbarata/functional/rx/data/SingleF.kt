package com.github.fsbarata.functional.rx.data

import com.github.fsbarata.functional.Context
import com.github.fsbarata.functional.control.*
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.core.SingleObserver

class SingleF<A>(private val wrapped: Single<A>): Single<A>(),
	MonadZip<SingleF<*>, A> {
	override val scope get() = Companion

	override fun subscribeActual(observer: SingleObserver<in A>) {
		wrapped.subscribe(observer)
	}

	override fun <B> map(f: (A) -> B) = wrapped.map(f).f()

	override infix fun <B> bind(f: (A) -> Context<SingleF<*>, B>): SingleF<B> =
		flatMap { f(it).asSingle }

	fun <B> flatMap(f: (A) -> Single<B>): SingleF<B> =
		wrapped.flatMap(f).f()

	override fun <B, R> zipWith(other: MonadZip<SingleF<*>, B>, f: (A, B) -> R) =
		(this as Single<A>).zipWith(other.asSingle, f).f()

	companion object: Monad.Scope<SingleF<*>> {
		override fun <A> just(a: A) = Single.just(a).f()
	}
}

fun <A> Single<A>.f() = SingleF(this)

val <A> Context<SingleF<*>, A>.asSingle
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
