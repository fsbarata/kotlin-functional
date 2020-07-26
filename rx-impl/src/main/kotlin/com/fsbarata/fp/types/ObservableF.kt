package com.fsbarata.fp.types

import com.fsbarata.fp.concepts.Context
import com.fsbarata.fp.concepts.Functor
import com.fsbarata.fp.concepts.Monad
import com.fsbarata.fp.concepts.Monoid
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.ObservableSource
import io.reactivex.rxjava3.core.Observer

class ObservableF<A>(
		private val wrapped: Observable<A>
) : Observable<A>(),
		Monad<Observable<*>, A>,
		ObservableSource<A> {
	override fun subscribeActual(observer: Observer<in A>) {
		wrapped.subscribe(observer)
	}

	override fun <B> just(b: B): ObservableF<B> =
			Companion.just(b)

	override fun <B> map(f: (A) -> B) =
			wrapped.map(f).f()

	override fun <B> flatMap(f: (A) -> Functor<Observable<*>, B>): ObservableF<B> =
			wrapped.flatMap { f(it).asObservable }.f()

	companion object {
		fun <A> empty() = Observable.empty<A>().f()
		fun <A> just(a: A) = Observable.just(a).f()
	}
}

fun <A> Observable<A>.f() = ObservableF(this)

val <A> Context<Observable<*>, A>.asObservable
	get() = this as ObservableF<A>
