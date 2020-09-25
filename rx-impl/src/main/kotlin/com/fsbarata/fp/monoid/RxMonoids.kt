package com.fsbarata.fp.monoid

import com.fsbarata.fp.concepts.Monoid
import com.fsbarata.fp.concepts.Semigroup
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.kotlin.Observables

fun completableConcatMonoid() = object: Monoid<Completable> {
	override fun empty() = Completable.never()
	override fun Completable.combine(other: Completable) = concatWith(other)
}

fun completableMergeMonoid() = object: Monoid<Completable> {
	override fun empty() = Completable.never()
	override fun Completable.combine(other: Completable) = mergeWith(other)
}

fun <A> maybeCombineMonoid(semigroup: Semigroup<A>) = object: Monoid<Maybe<A>> {
	override fun empty() = Maybe.empty<A>()
	override fun Maybe<A>.combine(other: Maybe<A>) =
		with(semigroup) {
			flatMap { a ->
				other.map { a.combine(it) }
					.switchIfEmpty(this@combine)
			}.switchIfEmpty(other)
		}
}

fun <A> observableConcatMonoid() = object: Monoid<Observable<A>> {
	override fun empty() = Observable.empty<A>()
	override fun Observable<A>.combine(other: Observable<A>) = concatWith(other)
}

fun <A> observableMergeMonoid() = object: Monoid<Observable<A>> {
	override fun empty() = Observable.empty<A>()
	override fun Observable<A>.combine(other: Observable<A>) = mergeWith(other)
}

fun <A: Any> observableCombineLatestMonoid(monoid: Monoid<A>) = object: Monoid<Observable<A>> {
	override fun empty() = Observable.just(monoid.empty())
	override fun Observable<A>.combine(other: Observable<A>): Observable<A> =
		with(monoid) { Observables.combineLatest(this@combine, other) { a, b -> a.combine(b) } }
}

