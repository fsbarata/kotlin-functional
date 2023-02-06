package com.github.fsbarata.functional.data.rx

import com.github.fsbarata.functional.Context
import com.github.fsbarata.functional.control.MonadPlus
import com.github.fsbarata.functional.control.MonadZip
import com.github.fsbarata.functional.control.apFromLift2
import com.github.fsbarata.functional.data.Semigroup
import com.github.fsbarata.functional.data.id
import com.github.fsbarata.functional.data.maybe.Optional
import com.github.fsbarata.functional.data.maybe.toOptional
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Observer

class ObservableF<A: Any>(private val wrapped: Observable<A>): Observable<A>(),
	Context<ObservableContext, A> {
	override fun subscribeActual(observer: Observer<in A>) {
		wrapped.subscribe(observer)
	}

	companion object {
		fun <A: Any> empty(): ObservableF<A> = ObservableF(Observable.empty())
		fun <A: Any> just(a: A): ObservableF<A> = ObservableF(Observable.just(a))
	}
}

abstract class ObservableMonad: MonadPlus.Scope<ObservableContext>, MonadZip.Scope<ObservableContext> {
	override fun <A> empty(): ObservableF<A & Any> = ObservableF(Observable.empty())
	override fun <A> just(a: A): ObservableF<A & Any> = ObservableF(Observable.just(a!!))

	override fun <A> fromIterable(iterable: Iterable<A>): ObservableF<A & Any> =
		ObservableF(Observable.fromIterable(iterable as Iterable<A & Any>))

	override fun <A> fromSequence(sequence: Sequence<A>): ObservableF<A & Any> =
		fromIterable(sequence.asIterable())

	override fun <A> fromList(list: List<A>): ObservableF<A & Any> =
		fromIterable(list)

	override fun <A> fromOptional(optional: Optional<A>): ObservableF<A & Any> =
		optional.maybe(empty(), ObservableSwitchMonad::just)

	override fun <A, B> map(ca: Context<ObservableContext, A>, f: (A) -> B): Context<ObservableContext, B & Any> {
		return ObservableF(ca.asObservable.map { f(it)!! })
	}

	override fun <A> combine(
		fa1: Context<ObservableContext, A>,
		fa2: Context<ObservableContext, A>,
	): Context<ObservableContext, A & Any> {
		return ObservableF(
			Observable.concatArray(
				fa1 as ObservableF<A & Any>,
				fa2 as ObservableF<A & Any>,
			)
		)
	}

	override fun <A> filter(
		ca: Context<ObservableContext, A>,
		predicate: (A) -> Boolean,
	): Context<ObservableContext, A & Any> {
		return ObservableF(ca.asObservable.filter(predicate))
	}

	override fun <A, B: Any> mapNotNull(
		ca: Context<ObservableContext, A>,
		f: (A) -> B?,
	): Context<ObservableContext, B> {
		return ObservableF(ca.asObservable.mapNotNull(f))
	}

	override fun <A, B, R> zip(
		ca: Context<ObservableContext, A>,
		cb: Context<ObservableContext, B>,
		f: (A, B) -> R,
	): Context<ObservableContext, R & Any> {
		return ObservableF((ca as Observable<A & Any>).zipWith(cb as Observable<B & Any>) { a, b -> f(a, b)!! })
	}
}

object ObservableSwitchMonad: ObservableMonad() {
	override fun <A, B, R> lift2(
		fa: Context<ObservableContext, A>,
		fb: Context<ObservableContext, B>,
		f: (A, B) -> R,
	): Context<ObservableContext, R & Any> {
		return ObservableF(Observable.combineLatest(
			fa as Observable<A&Any>,
			fb as Observable<B&Any>,
		) { a, b -> f(a, b)!! })
	}

	override fun <A, R> ap(
		fa: Context<ObservableContext, A>,
		ff: Context<ObservableContext, (A) -> R>
	): Context<ObservableContext, R> {
		return apFromLift2(this, fa, ff)
	}

	override fun <A, B> bind(
		ca: Context<ObservableContext, A>,
		f: (A) -> Context<ObservableContext, B>,
	): Context<ObservableContext, B & Any> {
		return ObservableF(ca.asObservable.switchMap { f(it) as ObservableF<B & Any> })
	}
}

object ObservableConcatMonad: ObservableMonad() {
	override fun <A, B> bind(
		ca: Context<ObservableContext, A>,
		f: (A) -> Context<ObservableContext, B>,
	): Context<ObservableContext, B & Any> {
		return ObservableF(ca.asObservable.concatMap { f(it) as ObservableF<B & Any> })
	}
}

object ObservableMergeMonad: ObservableMonad() {
	override fun <A, B> bind(
		ca: Context<ObservableContext, A>,
		f: (A) -> Context<ObservableContext, B>,
	): Context<ObservableContext, B & Any> {
		return ObservableF(ca.asObservable.flatMap { f(it) as ObservableF<B & Any> })
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

fun <A: Any> Observable<A>.f(): ObservableF<A> = ObservableF(this)

internal typealias ObservableContext = ObservableF<*>

val <A> Context<ObservableContext, A>.asObservable: ObservableF<A & Any>
	get() = this as ObservableF<A & Any>
