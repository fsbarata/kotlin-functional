package com.fsbarata.fp.types

import com.fsbarata.fp.concepts.*
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.ObservableSource
import io.reactivex.rxjava3.core.Observer

class ObservableF<A>(
	private val wrapped: Observable<A>,
): Observable<A>(),
   Monad<ObservableF<*>, A>,
   ObservableSource<A> {
	override val scope get() = Companion

	override fun subscribeActual(observer: Observer<in A>) {
		wrapped.subscribe(observer)
	}

	override fun <B> map(f: (A) -> B) =
		wrapped.map(f).f()

	override fun <B> bind(f: (A) -> Functor<ObservableF<*>, B>): ObservableF<B> =
		flatMap { f(it).asObservable }

	fun <B> flatMap(f: (A) -> Observable<B>): ObservableF<B> =
		wrapped.flatMap(f).f()

	fun reduce(semigroup: Semigroup<A>) = with(semigroup) { reduce { a1, a2 -> a1.combine(a2) } }.f()
	fun fold(monoid: Monoid<A>) = with(monoid) { reduce(empty) { a1, a2 -> a1.combine(a2) } }.f()
	fun scan(semigroup: Semigroup<A>) = with(semigroup) { scan { a1, a2 -> a1.combine(a2) } }.f()
	fun scan(monoid: Monoid<A>) = with(monoid) { scan(empty) { a1, a2 -> a1.combine(a2) } }.f()

	companion object: Monad.Scope<ObservableF<*>> {
		fun <A> empty() = Observable.empty<A>().f()
		override fun <A> just(a: A) = Observable.just(a).f()
	}
}

fun <A> Observable<A>.f() = ObservableF(this)

val <A> Context<ObservableF<*>, A>.asObservable
	get() = this as ObservableF<A>
