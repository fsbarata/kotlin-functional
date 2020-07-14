package com.fsbarata.fp.types

import com.fsbarata.fp.concepts.Context
import com.fsbarata.fp.concepts.Functor
import com.fsbarata.fp.concepts.Monad
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

	override fun <B> flatMap(f: (A) -> Functor<Single<*>, B>): SingleF<B> =
			wrapped.flatMap { f(it).asSingle }.f()

	companion object {
		fun <A> just(a: A) = Single.just(a).f()
	}
}

fun <A> Single<A>.f() = SingleF(this)

val <A> Context<Single<*>, A>.asSingle
	get() = this as Single<A>
