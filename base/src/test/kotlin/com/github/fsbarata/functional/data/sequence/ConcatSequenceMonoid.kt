package com.github.fsbarata.functional.data.sequence

import com.github.fsbarata.functional.data.MonoidLaws
import org.junit.Assert
import org.junit.Test


class ConcatSequenceMonoid: MonoidLaws<SequenceF<Int>>(SequenceF.monoid()) {
	override val possibilities: Int = 10
	override fun factory(possibility: Int) = createSequence(possibility)

	override fun equals(a1: SequenceF<Int>, a2: SequenceF<Int>): Boolean =
		a1.toList() == a2.toList()

	@Test
	fun concats() {
		Assert.assertEquals(
			listOf("6", "5", "1", "1L", "ajfg"),
			sequenceOf("6", "5").f()
				.combineWith(sequenceOf("1", "1L", "ajfg").f())
				.toList()
		)
	}
}