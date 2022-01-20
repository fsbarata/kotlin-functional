package com.github.fsbarata.functional.data.sequence

fun createSequence(possibility: Int): SequenceF<Int> = when (possibility) {
	0 -> emptySequence()
	1 -> sequenceOf(3)
	2 -> sequenceOf(1, 4)
	else -> sequenceOf(2) + createSequence(possibility - 3)
}.f()

fun createNes(possibility: Int): NonEmptySequence<Int> = when (possibility) {
	0 -> NonEmptySequence.just(1)
	1 -> nonEmptySequenceOf(3, 2)
	else -> NonEmptySequence.of(1, createNes(possibility - 2))
}
