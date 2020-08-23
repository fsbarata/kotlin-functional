package com.fsbarata.fp.types

import com.fsbarata.fp.concepts.*
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

	fun reduce(semigroup: Semigroup<A>) = with(semigroup) { reduce { a1, a2 -> a1.combine(a2) } }.f()
	fun fold(initialValue: A, semigroup: Semigroup<A>) = with(semigroup) { reduce(initialValue) { a1, a2 -> a1.combine(a2) } }.f()
	fun fold(monoid: Monoid<A>) = with(monoid) { reduce(empty()) { a1, a2 -> a1.combine(a2) } }.f()
	fun scan(semigroup: Semigroup<A>) = with(semigroup) { scan { a1, a2 -> a1.combine(a2) } }.f()
	fun scan(initialValue: A, semigroup: Semigroup<A>) = with(semigroup) { scan(initialValue) { a1, a2 -> a1.combine(a2) } }.f()
	fun scan(monoid: Monoid<A>) = with(monoid) { scan(empty()) { a1, a2 -> a1.combine(a2) } }.f()

	companion object {
		fun <A> empty() = Observable.empty<A>().f()
		fun <A> just(a: A) = Observable.just(a).f()
	}
}

fun <A> Observable<A>.f() = ObservableF(this)

val <A> Context<Observable<*>, A>.asObservable
	get() = this as ObservableF<A>
