package com.github.fsbarata.functional.data.tree

fun createTreeSequence(possibility: Int): Tree<Int> = when (possibility) {
	0 -> Tree.just(5)
	1, 2, 3 -> Tree.of(2, sequenceOf(createTreeSequence(possibility - 1)))
	else -> Tree.of(3, sequenceOf(createTreeSequence(possibility - 2), createTreeSequence(possibility - 3)))
}
