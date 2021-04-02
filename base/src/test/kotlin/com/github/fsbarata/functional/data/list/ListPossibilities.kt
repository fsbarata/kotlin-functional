package com.github.fsbarata.functional.data.list

fun createList(possibility: Int): ListF<Int> = when (possibility) {
	0 -> emptyList()
	1 -> listOf(3)
	2 -> listOf(4, 1)
	else -> listOf(2) + createList(possibility - 3)
}.f()

fun createNel(possibility: Int): NonEmptyList<Int> = when (possibility) {
	0 -> NonEmptyList.just(1)
	1 -> nelOf(3, 2)
	else -> NonEmptyList.of(1, createNel(possibility - 2))
}
