package com.github.fsbarata.functional.kotlin

import com.github.fsbarata.functional.data.collection.NonEmptyCollection
import com.github.fsbarata.functional.data.id
import com.github.fsbarata.functional.data.list.*
import com.github.fsbarata.functional.data.maybe.Optional
import com.github.fsbarata.functional.data.set.NonEmptySet
import com.github.fsbarata.functional.data.set.toNes

fun <A> Iterable<A>.plusElementNel(item: A): NonEmptyList<A> =
	toNel()?.plus(item) ?: NonEmptyList.just(item)

fun <A> Iterable<A>.plusNel(other: NonEmptyCollection<A>): NonEmptyList<A> =
	toNel()?.plus(other) ?: other.toNel()

fun <A> Set<A>.plusElementNes(item: A): NonEmptySet<A> =
	toNes()?.plus(item) ?: NonEmptySet.just(item)

fun <A> Set<A>.plusNes(other: NonEmptyCollection<A>): NonEmptySet<A> =
	toNes()?.plus(other) ?: other.toNes()

inline fun <T, K> Iterable<T>.groupByNel(crossinline keySelector: (T) -> K): Map<K, NonEmptyList<T>> =
	groupByNel(keySelector, ::id)

inline fun <T, K, V> Iterable<T>.groupByNel(
	crossinline keySelector: (T) -> K,
	valueTransform: (T) -> V,
): Map<K, NonEmptyList<V>> =
	buildMap<K, Pair<V, ImmutableListBuildScope<V>>> {
		for (element in this@groupByNel) {
			val key = keySelector(element)
			val value = valueTransform(element)
			val accumulator = this[key]
			if (accumulator == null) put(key, Pair(value, ImmutableListBuildScope()))
			else accumulator.second.add(value)
		}
	}
		.mapValues { NonEmptyList(it.value.first, it.value.second.build()) }

fun <T> Iterable<T>.chunkedNel(size: Int): List<NonEmptyList<T>> =
	windowedNel(size, size, partialWindows = true)

fun <T> Iterable<T>.windowedNel(size: Int, step: Int = 1, partialWindows: Boolean = false): List<NonEmptyList<T>> =
	windowed(size, step, partialWindows) { it.toNel() ?: throw NoSuchElementException() }

inline fun <T, R> Iterable<T>.scanNel(initialValue: R, operation: (R, T) -> R) = NonEmptyList.of(
	initialValue,
	scan(initialValue, operation).drop(1)
)

inline fun <A, R: Any> Iterable<A>.mapNotNone(f: (A) -> Optional<R>): List<R> =
	mapNotNull { f(it).orNull() }

inline fun <A: Any> Iterable<Optional<A>>.filterNotNone(): List<A> =
	mapNotNone(::id)

inline fun <A, R: Any> Iterable<A>.mapNotNullToSet(f: (A) -> R?): Set<R> =
	mapNotNullTo(mutableSetOf(), f)

inline fun <A: Any> Iterable<A?>.filterNotNullToSet(): Set<A> =
	filterNotNullTo(mutableSetOf())

inline fun <A, R: Any> Iterable<A>.mapNotNoneToSet(f: (A) -> Optional<R>): Set<R> =
	mapNotNullToSet { f(it).orNull() }

inline fun <A: Any> Iterable<Optional<A>>.filterNotNoneToSet(): Set<A> =
	mapNotNoneToSet(::id)
