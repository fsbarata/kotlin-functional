package com.fsbarata.fp.types

import com.fsbarata.fp.concepts.*
import io.reactivex.Observable
import io.reactivex.ObservableSource
import io.reactivex.Observer

class ObservableF<A>(
		private val wrapped: Observable<A>
) : Observable<A>(),
		Monad<Observable<*>, A>,
		Monoid<ObservableF<A>>,
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

	override fun empty() = empty<A>()

	override fun append(a: ObservableF<A>) = concatWith(a).f()

	companion object {
		fun <A> empty() = Observable.empty<A>().f()
		fun <A> just(a: A) = Observable.just(a).f()
	}
}

fun <A> Observable<A>.f() = ObservableF(this)

val <A> Context<Observable<*>, A>.asObservable
	get() = this as Observable<A>
