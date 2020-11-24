package com.github.fsbarata.functional.data.rx

import com.github.fsbarata.functional.data.SemigroupLaws

class ObservableSemigroupTest:
	SemigroupLaws<ObservableF<Int>> {

	override val possibilities: Int = 10
	override fun factory(possibility: Int): ObservableF<Int> = observableFactory(possibility)

	override fun assertEqual(a1: ObservableF<Int>, a2: ObservableF<Int>) {
		assertEqualObs(a1, a2)
	}
}