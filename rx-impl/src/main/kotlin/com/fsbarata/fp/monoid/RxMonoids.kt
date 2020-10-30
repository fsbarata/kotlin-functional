package com.fsbarata.fp.monoid

import com.fsbarata.fp.concepts.Monoid
import com.fsbarata.fp.concepts.Semigroup
import com.fsbarata.fp.concepts.monoid
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.kotlin.Observables

fun concatCompletableMonoid() = monoid(Completable.complete(), Completable::concatWith)
fun mergeCompletableMonoid() = monoid(Completable.never(), Completable::mergeWith)
fun <A> concatObservableMonoid() = monoid(Observable.empty<A>(), Observable<A>::concatWith)
fun <A> mergeObservableMonoid() = monoid(Observable.never<A>(), Observable<A>::mergeWith)

fun <A> maybeMonoid(sg: Semigroup<A>): Monoid<Maybe<A>> =
	monoid(Maybe.empty()) { maybe1, maybe2 ->
		maybe1
			.flatMapSingle { a ->
				maybe2.map { sg.combine(a, it) }
					.defaultIfEmpty(a)
			}
			.switchIfEmpty(maybe2)
	}

fun <A: Any> combineLatestObservableMonoid(monoid: Monoid<A>): Monoid<Observable<A>> =
	monoid(Observable.just(monoid.empty)) { obs1, obs2 ->
		Observables.combineLatest(obs1, obs2) { a1: A, a2: A -> monoid.combine(a1, a2) }
	}


