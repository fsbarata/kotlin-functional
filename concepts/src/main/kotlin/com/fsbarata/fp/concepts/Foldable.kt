package com.fsbarata.fp.concepts

interface Foldable<T> {
	fun reduceOrNull(accumulator: (T, T) -> T): T? =
			fold<T?>(null) { currentValue, newValue ->
				accumulator(currentValue ?: return@fold newValue, newValue)
			}

	fun reduce(accumulator: (T, T) -> T): T =
			reduceOrNull(accumulator) ?: throw NoSuchElementException()

	fun <R> fold(initialValue: R, accumulator: (R, T) -> R): R
}