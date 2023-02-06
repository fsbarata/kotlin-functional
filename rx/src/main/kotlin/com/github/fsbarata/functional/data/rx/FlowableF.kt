package com.github.fsbarata.functional.data.rx

import com.github.fsbarata.functional.data.Semigroup
import com.github.fsbarata.functional.data.id
import com.github.fsbarata.functional.data.maybe.Optional
import com.github.fsbarata.functional.data.maybe.toOptional
import io.reactivex.rxjava3.core.Flowable

fun <A: Semigroup<A>> Flowable<A>.reduce() = reduce { a1, a2 -> a1.concatWith(a2) }.f()
fun <A: Semigroup<A>> Flowable<A>.fold(initialValue: A) = reduce(initialValue) { a1, a2 -> a1.concatWith(a2) }.f()
fun <A: Semigroup<A>> Flowable<A>.scan() = scan { a1, a2 -> a1.concatWith(a2) }
fun <A: Semigroup<A>> Flowable<A>.scan(initialValue: A) = scan(initialValue) { a1, a2 -> a1.concatWith(a2) }

fun <A: Any, R: Any> Flowable<A>.mapNotNull(f: (A) -> R?): Flowable<R> =
	mapNotNone { f(it).toOptional() }

fun <A: Any, R: Any> Flowable<A>.mapNotNone(f: (A) -> Optional<R>): Flowable<R> =
	map(f).filter { it.isPresent() }
		.map { it.orNull()!! }

fun <A: Any> Flowable<Optional<A>>.filterNotNone(): Flowable<A> =
	mapNotNone(id())

fun <A: Any> Flowable<A>.partition(predicate: (A) -> Boolean): Pair<Flowable<A>, Flowable<A>> =
	Pair(filter(predicate), filter { !predicate(it) })
