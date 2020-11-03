package com.github.fsbarata.functional.kotlin

import com.github.fsbarata.functional.data.list.NonEmptyList
import com.github.fsbarata.functional.data.list.nelOf
import com.github.fsbarata.functional.data.list.plus
import com.github.fsbarata.functional.data.list.toNel

fun <A> Iterable<A>.plusNel(item: A): NonEmptyList<A> =
	toNel()?.plus(item) ?: NonEmptyList.just(item)

fun <A> Iterable<A>.plusNel(other: NonEmptyList<A>): NonEmptyList<A> = plus(other)

fun <T, K> Iterable<T>.groupByNel(keySelector: (T) -> K): Map<K, NonEmptyList<T>> =
	groupByNel(keySelector) { it }

fun <T, K, V> Iterable<T>.groupByNel(keySelector: (T) -> K, valueSelector: (T) -> V): Map<K, NonEmptyList<V>> =
	groupingBy(keySelector)
		.aggregate { _, accumulator, element, _ ->
			val value = valueSelector(element)
			accumulator?.plus(value) ?: nelOf(value)
		}

fun <T, R> Iterable<T>.scanNel(initialValue: R, operation: (R, T) -> R) = NonEmptyList.of(
	initialValue,
	scan(initialValue, operation).drop(1)
)
