package com.github.fsbarata.functional.data.rx

import com.github.fsbarata.functional.data.Monoid
import com.github.fsbarata.functional.data.Semigroup
import com.github.fsbarata.functional.data.monoidOf
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.core.Observable

fun concatCompletableMonoid() = monoidOf(Completable.complete(), Completable::concatWith)
fun mergeCompletableMonoid() = monoidOf(Completable.never(), Completable::mergeWith)
fun <A: Any> concatObservableMonoid() = monoidOf(Observable.empty(), Observable<A>::concatWith)

fun <A: Semigroup<A>> maybeMonoid(): Monoid<Maybe<A>> =
	monoidOf(Maybe.empty()) { maybe1, maybe2 ->
		maybe1
			.flatMapSingle { a ->
				maybe2.map { a.concatWith(it) }
					.defaultIfEmpty(a)
			}
			.switchIfEmpty(maybe2)
	}


