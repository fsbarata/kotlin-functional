package com.github.fsbarata.functional.kotlin

import com.github.fsbarata.functional.data.collection.NonEmptyCollection
import com.github.fsbarata.functional.data.id
import com.github.fsbarata.functional.data.list.*
import com.github.fsbarata.functional.data.maybe.Optional
import com.github.fsbarata.functional.data.set.NonEmptySet
import com.github.fsbarata.functional.data.set.toNes

fun <A> Iterable<A>.plusElementNel(element: A): NonEmptyList<A> {
	val iterator = iterator()
	if (!iterator.hasNext()) return NonEmptyList.just(element)
	val head = iterator.next()
	val tail = buildListF {
		while (iterator.hasNext()) add(iterator.next())
		add(element)
	}
	return NonEmptyList(head, tail)
}

fun <A> List<A>.plusElementNel(element: A): NonEmptyList<A> {
	if (isEmpty()) return NonEmptyList.just(element)
	val iterator = iterator()
	val head = iterator.next()
	val tail = buildListF(size) {
		while (iterator.hasNext()) add(iterator.next())
		add(element)
	}
	return NonEmptyList(head, tail)
}

fun <A> Iterable<A>.plusNel(elements: NonEmptyCollection<A>): NonEmptyList<A> {
	val iterator = iterator()
	if (!iterator.hasNext()) return elements.toNel()
	val head = iterator.next()
	val tail = buildListF {
		while (iterator.hasNext()) add(iterator.next())
		addAll(elements)
	}
	return NonEmptyList(head, tail)
}

fun <A> List<A>.plusNel(elements: NonEmptyCollection<A>): NonEmptyList<A> {
	val iterator = iterator()
	if (!iterator.hasNext()) return elements.toNel()
	val head = iterator.next()
	val tail = buildListF(size) {
		while (iterator.hasNext()) add(iterator.next())
		addAll(elements)
	}
	return NonEmptyList(head, tail)
}

fun <A> Set<A>.plusElementNes(element: A): NonEmptySet<A> {
	val iterator = iterator()
	if (!iterator.hasNext()) return NonEmptySet.just(element)

	val head = iterator.next()
	val tail = buildSet(size + 1) {
		while (iterator.hasNext()) add(iterator.next())
		if (head != element) add(element)
	}
	return NonEmptySet(head, SetF(tail))
}

fun <A> Set<A>.plusNes(elements: NonEmptyCollection<A>): NonEmptySet<A> {
	val iterator = iterator()
	if (!iterator.hasNext()) return elements.toNes()

	val head = iterator.next()
	val tail = buildSet(size + elements.size) {
		add(head)
		while (iterator.hasNext()) add(iterator.next())
		addAll(elements)
		remove(head)
	}
	return NonEmptySet(head, SetF(tail))
}

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

inline fun <A> Iterable<A>.firstOrError(): A = first()
inline fun <A> List<A>.firstOrError(): A = first()
inline fun <A> Iterable<A>.lastOrError(): A = last()
inline fun <A> List<A>.lastOrError(): A = last()