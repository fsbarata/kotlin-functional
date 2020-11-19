package com.github.fsbarata.functional.rx.data

import com.github.fsbarata.functional.Context
import com.github.fsbarata.functional.control.Alternative
import com.github.fsbarata.functional.control.Applicative
import com.github.fsbarata.functional.control.Monad
import com.github.fsbarata.functional.control.MonadZip
import com.github.fsbarata.functional.data.Monoid
import com.github.fsbarata.functional.data.Semigroup
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.ObservableSource
import io.reactivex.rxjava3.core.Observer

class ObservableF<A>(
	private val wrapped: Observable<A>,
): Observable<A>(),
	Monad<ObservableContext, A>,
	MonadZip<ObservableContext, A>,
	Alternative<ObservableContext, A>,
	Semigroup<ObservableF<A>>,
	ObservableSource<A> {
	override val scope get() = ObservableF

	override fun subscribeActual(observer: Observer<in A>) {
		wrapped.subscribe(observer)
	}

	override fun <B> map(f: (A) -> B) =
		wrapped.map(f).f()

	override fun <B> ap(ff: Applicative<ObservableContext, (A) -> B>) =
		combineLatest(
			this,
			ff.asObservable,
		) { a, f -> f(a) }
			.f()

	override fun <B, R> lift2(
		fb: Applicative<ObservableContext, B>,
		f: (A, B) -> R
	) = combineLatest(this, fb.asObservable, f).f()

	override infix fun <B> bind(f: (A) -> Context<ObservableContext, B>): ObservableF<B> =
		flatMap { f(it).asObservable }

	fun <B> flatMap(f: (A) -> Observable<B>): ObservableF<B> =
		wrapped.flatMap(f).f()

	fun fold(monoid: Monoid<A>) = super.reduce(monoid.empty, monoid::combine).f()
	fun scan(monoid: Monoid<A>) = super.scan(monoid.empty, monoid::combine).f()

	override fun combineWith(other: ObservableF<A>) = mergeWith(other).f()
	override fun associateWith(other: Alternative<ObservableContext, A>) =
		combineWith(other.asObservable)

	override fun <B, R> zipWith(other: MonadZip<ObservableContext, B>, f: (A, B) -> R) =
		(this as Observable<A>).zipWith(other.asObservable, f).f()

	companion object: Monad.Scope<ObservableContext>, Alternative.Scope<ObservableContext> {
		override fun <A> empty() = Observable.empty<A>().f()
		override fun <A> just(a: A) = Observable.just(a).f()
	}
}

fun <A: Semigroup<A>> Observable<A>.reduce() = reduce { a1, a2 -> a1.combineWith(a2) }.f()
fun <A: Semigroup<A>> Observable<A>.fold(initialValue: A) = reduce(initialValue) { a1, a2 -> a1.combineWith(a2) }.f()
fun <A: Semigroup<A>> Observable<A>.scan() = scan { a1, a2 -> a1.combineWith(a2) }.f()
fun <A: Semigroup<A>> Observable<A>.scan(initialValue: A) = scan(initialValue) { a1, a2 -> a1.combineWith(a2) }.f()

fun <A> Observable<A>.f() = ObservableF(this)

internal typealias ObservableContext = ObservableF<*>

val <A> Context<ObservableContext, A>.asObservable
	get() = this as ObservableF<A>
