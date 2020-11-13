package com.github.fsbarata.functional.data.sequence

import com.github.fsbarata.functional.data.test.MonoidLaws
import org.junit.Assert
import org.junit.Test


class ConcatSequenceMonoid: MonoidLaws<Sequence<Int>>(
	SequenceF.concatMonoid(),
) {
	override val possibilities: Int = 25
	override fun nonEmpty(possibility: Int) =
		(0..possibility).map { it % 13 }
			.asSequence()

	override fun equals(a1: Sequence<Int>, a2: Sequence<Int>): Boolean =
		a1.toList() == a2.toList()

	@Test
	fun concats() {
		Assert.assertEquals(
			listOf("6", "5", "1", "1L", "ajfg"),
			SequenceF.concatMonoid<String>()
				.combine(sequenceOf("6", "5"), sequenceOf("1", "1L", "ajfg"))
				.toList()
		)
	}
}