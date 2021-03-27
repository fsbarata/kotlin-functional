package com.github.fsbarata.functional.data.rx

import com.github.fsbarata.functional.Context
import com.github.fsbarata.functional.control.*
import com.github.fsbarata.functional.data.Monoid
import com.github.fsbarata.functional.data.Semigroup
import com.github.fsbarata.functional.data.maybe.Optional
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.ObservableSource
import io.reactivex.rxjava3.core.Observer

class ObservableF<A>(private val wrapped: Observable<A>): Observable<A>(),
	MonadZip<ObservableContext, A>,
	MonadPlus<ObservableContext, A>,
	Semigroup<ObservableF<A>> {
	override val scope get() = ObservableF

	override fun subscribeActual(observer: Observer<in A>) {
		wrapped.subscribe(observer)
	}

	override fun <B> map(f: (A) -> B) =
		wrapped.map(f).f()

	override fun <B> ap(ff: Applicative<ObservableContext, (A) -> B>) =
		combineLatest(
			ff.asObservable,
			this,
		) { f, a -> f(a) }
			.f()

	override fun <B, R> lift2(
		fb: Applicative<ObservableContext, B>,
		f: (A, B) -> R,
	) = lift2(f).invoke(this, fb.asObservable)

	override infix fun <B> bind(f: (A) -> Context<ObservableContext, B>): ObservableF<B> =
		wrapped.switchMap { f(it).asObservable }.f()

	fun <B> flatMap(f: (A) -> ObservableSource<B>): ObservableF<B> =
		wrapped.flatMap(f).f()

	override fun filter(predicate: (A) -> Boolean) =
		wrapped.filter(predicate).f()

	override fun partition(predicate: (A) -> Boolean) =
		Pair(filter(predicate), filter { !predicate(it) })

	override fun <B: Any> mapNotNull(f: (A) -> B?) =
		flatMapMaybe { Maybe.just(f(it) ?: return@flatMapMaybe Maybe.empty()) }.f()

	override fun <B: Any> mapNotNone(f: (A) -> Optional<B>) =
		flatMapMaybe { Maybe.just(f(it).orNull() ?: return@flatMapMaybe Maybe.empty()) }.f()

	fun fold(monoid: Monoid<A>) = super.reduce(monoid.empty, monoid::combine).f()
	fun scan(monoid: Monoid<A>) = super.scan(monoid.empty, monoid::combine).f()

	override fun combineWith(other: ObservableF<A>) = mergeWith(other).f()
	override fun associateWith(other: Context<ObservableContext, A>) =
		combineWith(other.asObservable)

	override fun <B, R> zipWith(other: MonadZip<ObservableContext, B>, f: (A, B) -> R) =
		(this as Observable<A>).zipWith(other.asObservable, f).f()

	companion object: MonadPlus.Scope<ObservableContext> {
		override fun <A> empty() = Observable.empty<A>().f()
		override fun <A> just(a: A) = Observable.just(a).f()

		override fun <A> fromList(list: List<A>) = fromIterable(list).f()
		override fun <A> fromOptional(optional: Optional<A>) = optional.maybe(empty(), ::just)
	}
}

fun <A: Semigroup<A>> Observable<A>.reduce() = reduce { a1, a2 -> a1.combineWith(a2) }.f()
fun <A: Semigroup<A>> Observable<A>.fold(initialValue: A) = reduce(initialValue) { a1, a2 -> a1.combineWith(a2) }.f()
fun <A: Semigroup<A>> Observable<A>.scan() = scan { a1, a2 -> a1.combineWith(a2) }.f()
fun <A: Semigroup<A>> Observable<A>.scan(initialValue: A) = scan(initialValue) { a1, a2 -> a1.combineWith(a2) }.f()

fun <A> Observable<A>.f() = ObservableF(this)
fun <A, R> Observable<A>.f(block: ObservableF<A>.() -> Context<ObservableContext, R>) =
	f().block().asObservable

internal typealias ObservableContext = ObservableF<*>

val <A> Context<ObservableContext, A>.asObservable
	get() = this as ObservableF<A>

operator fun <A, B, R> Lift2<A, B, R>.invoke(
	obs1: Observable<A>,
	obs2: Observable<B>,
): ObservableF<R> = Observable.combineLatest(obs1, obs2, f).f()

operator fun <A, B, C, R> Lift3<A, B, C, R>.invoke(
	obs1: Observable<A>,
	obs2: Observable<B>,
	obs3: Observable<C>,
): ObservableF<R> = Observable.combineLatest(obs1, obs2, obs3, f).f()

operator fun <A, B, C, D, R> Lift4<A, B, C, D, R>.invoke(
	obs1: Observable<A>,
	obs2: Observable<B>,
	obs3: Observable<C>,
	obs4: Observable<D>,
): ObservableF<R> = Observable.combineLatest(obs1, obs2, obs3, obs4, f).f()
