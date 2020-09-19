package com.fsbarata.fp.types

import com.fsbarata.fp.concepts.*
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.core.SingleObserver
import io.reactivex.rxjava3.core.SingleSource

class SingleF<A>(
		private val wrapped: Single<A>
) : Single<A>(),
		Monad<Single<*>, A>,
		SingleSource<A> {
	override fun subscribeActual(observer: SingleObserver<in A>) {
		wrapped.subscribe(observer)
	}

	override fun <B> just(b: B): SingleF<B> = Companion.just(b)

	override fun <B> map(f: (A) -> B) = wrapped.map(f).f()

	override fun <B> bind(f: (A) -> Functor<Single<*>, B>): SingleF<B> =
			flatMap { f(it).asSingle }

	fun <B> flatMap(f: (A) -> Single<B>): SingleF<B> =
			wrapped.flatMap(f).f()

	companion object {
		fun <A> just(a: A) = Single.just(a).f()
	}
}

fun <A> Single<A>.f() = SingleF(this)

val <A> Context<Single<*>, A>.asSingle
	get() = this as SingleF<A>
