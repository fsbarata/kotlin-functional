package com.github.fsbarata.functional.rx.data

import com.github.fsbarata.functional.control.Context
import com.github.fsbarata.functional.control.Monad
import com.github.fsbarata.functional.control.MonadZip
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.core.SingleObserver
import io.reactivex.rxjava3.core.SingleSource

class SingleF<A>(
	private val wrapped: Single<A>,
): Single<A>(),
   Monad<SingleF<*>, A>,
   MonadZip<SingleF<*>, A>,
   SingleSource<A> {
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
