package com.fsbarata.fp.types

import com.fsbarata.fp.concepts.*
import com.fsbarata.fp.data.Monoid
import com.fsbarata.fp.data.Semigroup
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.ObservableSource
import io.reactivex.rxjava3.core.Observer

class ObservableF<A>(
	private val wrapped: Observable<A>,
): Observable<A>(),
   Monad<ObservableF<*>, A>,
   MonadZip<ObservableF<*>, A>,
   ObservableSource<A> {
	override val scope get() = Companion

	override fun subscribeActual(observer: Observer<in A>) {
		wrapped.subscribe(observer)
	}

	override fun <B> map(f: (A) -> B) =
		wrapped.map(f).f()

	override fun <B> bind(f: (A) -> Context<ObservableF<*>, B>): ObservableF<B> =
		flatMap { f(it).asObservable }

	fun <B> flatMap(f: (A) -> Observable<B>): ObservableF<B> =
		wrapped.flatMap(f).f()

	fun reduce(semigroup: Semigroup<A>) = super.reduce(semigroup::combine).f()
	fun fold(monoid: Monoid<A>) = super.reduce(monoid.empty, monoid::combine).f()
	fun scan(semigroup: Semigroup<A>) = super.scan(semigroup::combine).f()
	fun scan(monoid: Monoid<A>) = super.scan(monoid.empty, monoid::combine).f()

	override fun <B, R> zipWith(other: MonadZip<ObservableF<*>, B>, f: (A, B) -> R) =
		(this as Observable<A>).zipWith(other.asObservable, f).f()

	companion object: Monad.Scope<ObservableF<*>> {
		fun <A> empty() = Observable.empty<A>().f()
		override fun <A> just(a: A) = Observable.just(a).f()
	}
}

fun <A> Observable<A>.f() = ObservableF(this)

val <A> Context<ObservableF<*>, A>.asObservable
	get() = this as ObservableF<A>
