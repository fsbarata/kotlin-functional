package com.github.fsbarata.functional.kotlin

import com.github.fsbarata.functional.data.collection.NonEmptyCollection
import com.github.fsbarata.functional.data.list.NonEmptyList
import com.github.fsbarata.functional.data.list.nelOf
import com.github.fsbarata.functional.data.list.toNel
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
	groupByNel(keySelector) { it }

inline fun <T, K, V> Iterable<T>.groupByNel(
	crossinline keySelector: (T) -> K,
	valueTransform: (T) -> V,
): Map<K, NonEmptyList<V>> =
	groupingBy(keySelector)
		.aggregate { _, accumulator, element, _ ->
			val value = valueTransform(element)
			accumulator?.plus(value) ?: nelOf(value)
		}

fun <T> Iterable<T>.chunkedNel(size: Int): List<NonEmptyList<T>> =
	windowedNel(size, size, partialWindows = true)

fun <T> Iterable<T>.windowedNel(size: Int, step: Int = 1, partialWindows: Boolean = false): List<NonEmptyList<T>> =
	windowed(size, step, partialWindows) { it.toNel() ?: throw NoSuchElementException() }

inline fun <T, R> Iterable<T>.scanNel(initialValue: R, operation: (R, T) -> R) = NonEmptyList.of(
	initialValue,
	scan(initialValue, operation).drop(1)
)
