package com.github.fsbarata.functional.rx.monoids

import com.github.fsbarata.functional.data.Monoid
import com.github.fsbarata.functional.data.Semigroup
import com.github.fsbarata.functional.data.monoid
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.core.Observable

fun concatCompletableMonoid() = monoid(Completable.complete(), Completable::concatWith)
fun mergeCompletableMonoid() = monoid(Completable.never(), Completable::mergeWith)
fun <A> concatObservableMonoid() = monoid(Observable.empty(), Observable<A>::concatWith)
fun <A> mergeObservableMonoid() = monoid(Observable.never(), Observable<A>::mergeWith)

fun <A: Semigroup<A>> maybeMonoid(): Monoid<Maybe<A>> =
	monoid(Maybe.empty()) { maybe1, maybe2 ->
		maybe1
			.flatMapSingle { a ->
				maybe2.map { a.combineWith(it) }
					.defaultIfEmpty(a)
			}
			.switchIfEmpty(maybe2)
	}

fun <A: Any> combineLatestObservableMonoid(monoid: Monoid<A>): Monoid<Observable<A>> =
	monoid(Observable.just(monoid.empty)) { obs1, obs2 ->
		Observable.combineLatest(obs1, obs2) { a1: A, a2: A -> monoid.combine(a1, a2) }
	}


