package com.github.fsbarata.functional.data.rx

import com.github.fsbarata.functional.Context
import com.github.fsbarata.functional.control.*
import com.github.fsbarata.functional.data.Functor
import com.github.fsbarata.functional.data.Monoid
import com.github.fsbarata.functional.data.Semigroup
import com.github.fsbarata.functional.data.id
import com.github.fsbarata.functional.data.maybe.Optional
import com.github.fsbarata.functional.data.maybe.toOptional
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

	override fun <B> ap(ff: Functor<ObservableContext, (A) -> B>) =
		combineLatest(
			ff.asObservable,
			this,
		) { f, a -> f(a) }
			.f()

	override fun <B, R> lift2(
		fb: Functor<ObservableContext, B>,
		f: (A, B) -> R,
	) = combineLatest(this, fb.asObservable, f).f()

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

	fun fold(monoid: Monoid<A>): SingleF<A> = super.reduce(monoid.empty, monoid::combine).f()
	fun scan(monoid: Monoid<A>): ObservableF<A> = super.scan(monoid.empty, monoid::combine).f()

	override fun concatWith(other: ObservableF<A>): ObservableF<A> = super.concatWith(other).f()
	override fun associateWith(other: Context<ObservableContext, A>) =
		concatWith(other.asObservable)

	override fun <B, R> zipWith(other: Functor<ObservableContext, B>, f: (A, B) -> R) =
		(this as Observable<A>).zipWith(other.asObservable, f).f()

	companion object: MonadPlus.Scope<ObservableContext> {
		override fun <A> empty() = Observable.empty<A>().f()
		override fun <A> just(a: A) = Observable.just(a).f()

		override fun <A> fromIterable(iterable: Iterable<A>) = Observable.fromIterable(iterable).f()
		override fun <A> fromSequence(sequence: Sequence<A>) = fromIterable(sequence.asIterable())
		override fun <A> fromList(list: List<A>) = fromIterable(list)
		override fun <A> fromOptional(optional: Optional<A>) = optional.maybe(empty(), ::just)
	}
}

fun <A: Semigroup<A>> Observable<A>.reduce() = reduce { a1, a2 -> a1.concatWith(a2) }.f()
fun <A: Semigroup<A>> Observable<A>.fold(initialValue: A) = reduce(initialValue) { a1, a2 -> a1.concatWith(a2) }.f()
fun <A: Semigroup<A>> Observable<A>.scan() = scan { a1, a2 -> a1.concatWith(a2) }.f()
fun <A: Semigroup<A>> Observable<A>.scan(initialValue: A) = scan(initialValue) { a1, a2 -> a1.concatWith(a2) }.f()

fun <A: Any, R: Any> Observable<A>.mapNotNull(f: (A) -> R?): Observable<R> =
	mapNotNone { f(it).toOptional() }

fun <A: Any, R: Any> Observable<A>.mapNotNone(f: (A) -> Optional<R>): Observable<R> =
	map(f).filter { it.isPresent() }
		.map { it.orNull()!! }

fun <A: Any> Observable<Optional<A>>.filterNotNone(): Observable<A> =
	mapNotNone(id())

fun <A: Any> Observable<A>.partition(predicate: (A) -> Boolean): Pair<Observable<A>, Observable<A>> =
	Pair(filter(predicate), filter { !predicate(it) })

fun <A> Observable<A>.f() = ObservableF(this)
fun <A: Any, R: Any> Observable<A>.f(block: ObservableF<A>.() -> Context<ObservableContext, R>): ObservableF<R> =
	f().block().asObservable

internal typealias ObservableContext = ObservableF<*>

val <A> Context<ObservableContext, A>.asObservable
	get() = this as ObservableF<A>

operator fun <A: Any, B: Any, R: Any> Lift2<A, B, R>.invoke(
	obs1: Observable<A>,
	obs2: Observable<B>,
): ObservableF<R> = Observable.combineLatest(obs1, obs2, f).f()

operator fun <A: Any, B: Any, C: Any, R: Any> Lift3<A, B, C, R>.invoke(
	obs1: Observable<A>,
	obs2: Observable<B>,
	obs3: Observable<C>,
): ObservableF<R> = Observable.combineLatest(obs1, obs2, obs3, f).f()

operator fun <A: Any, B: Any, C: Any, D: Any, R: Any> Lift4<A, B, C, D, R>.invoke(
	obs1: Observable<A>,
	obs2: Observable<B>,
	obs3: Observable<C>,
	obs4: Observable<D>,
): ObservableF<R> = Observable.combineLatest(obs1, obs2, obs3, obs4, f).f()
