package com.github.fsbarata.functional.rx.monoids

import com.github.fsbarata.functional.data.Monoid
import com.github.fsbarata.functional.data.Semigroup
import com.github.fsbarata.functional.data.monoid
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.core.Observable

fun concatCompletableMonoid() = monoid(Completable.complete(), Completable::concatWith)
fun mergeCompletableMonoid() = monoid(Completable.never(), Completable::mergeWith)
fun <A: Any> concatObservableMonoid() = monoid(Observable.empty(), Observable<A>::concatWith)

fun <A: Semigroup<A>> maybeMonoid(): Monoid<Maybe<A>> =
	monoid(Maybe.empty()) { maybe1, maybe2 ->
		maybe1
			.flatMapSingle { a ->
				maybe2.map { a.concatWith(it) }
					.defaultIfEmpty(a)
			}
			.switchIfEmpty(maybe2)
	}


