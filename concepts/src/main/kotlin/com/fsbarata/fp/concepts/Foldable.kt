package com.fsbarata.fp.concepts

interface Foldable<T> {
	fun <R> fold(initialValue: R, accumulator: (R, T) -> R): R
}

fun <T, R> Foldable<T>.scan(initialValue: R, accumulator: (R, T) -> R) =
		fold(Pair(initialValue, listOf(initialValue))) { (a, list), v ->
			val newValue = accumulator(a, v)
			Pair(newValue, list + newValue)
		}.second
