package com.fsbarata.fp.monoid

import com.fsbarata.fp.concepts.Monoid
import com.fsbarata.fp.concepts.Semigroup
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.kotlin.Observables


fun <A> maybeMonoid(semigroup: Semigroup<A>) = object : Monoid<Maybe<A>> {
	override fun empty() = Maybe.empty<A>()
	override fun Maybe<A>.combine(other: Maybe<A>) =
			with(semigroup) {
				flatMap { a -> other.map { a.combine(it) } }
			}
}

fun <A> observableConcatMonoid() = object : Monoid<Observable<A>> {
	override fun empty() = Observable.empty<A>()
	override fun Observable<A>.combine(other: Observable<A>) = concatWith(other)
}

fun <A : Any> observableZipMonoid(semigroup: Semigroup<A>) = object : Monoid<Observable<A>> {
	override fun empty() = Observable.empty<A>()
	override fun Observable<A>.combine(other: Observable<A>): Observable<A> =
			with(semigroup) { Observables.zip(this@combine, other) { a, b -> a.combine(b) } }
}
