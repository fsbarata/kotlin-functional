package com.github.fsbarata.functional.data.tree

fun createTreeSequence(possibility: Int): TreeSequence<Int> = when (possibility) {
	0 -> TreeSequence.just(5)
	1, 2, 3 -> TreeSequence(2, sequenceOf(createTreeSequence(possibility - 1)))
	else -> TreeSequence(3, sequenceOf(createTreeSequence(possibility - 2), createTreeSequence(possibility - 3)))
}
