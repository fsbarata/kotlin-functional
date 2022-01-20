package com.github.fsbarata.functional.data.sequence

import com.github.fsbarata.functional.data.SemigroupLaws

class SequenceFSemigroupTest: SemigroupLaws<SequenceF<Int>> {
	override val possibilities = 10
	override fun factory(possibility: Int) = createSequence(possibility)

	override fun equals(a1: SequenceF<Int>, a2: SequenceF<Int>): Boolean =
		a1.toList() == a2.toList()
}
