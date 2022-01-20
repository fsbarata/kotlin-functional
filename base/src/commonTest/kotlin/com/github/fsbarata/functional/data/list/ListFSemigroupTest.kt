package com.github.fsbarata.functional.data.list

import com.github.fsbarata.functional.data.SemigroupLaws

class ListFSemigroupTest: SemigroupLaws<ListF<Int>> {
	override val possibilities = 10
	override fun factory(possibility: Int) = createList(possibility)
}