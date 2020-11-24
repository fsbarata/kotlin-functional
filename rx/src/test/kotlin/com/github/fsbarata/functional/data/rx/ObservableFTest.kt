package com.github.fsbarata.functional.data.rx

import com.github.fsbarata.functional.control.MonadPlusLaws
import com.github.fsbarata.functional.control.MonadZipLaws
import com.github.fsbarata.functional.data.Functor
import org.junit.Ignore
import org.junit.Test

class ObservableFTest:
	MonadPlusLaws<ObservableContext>,
	MonadZipLaws<ObservableContext> {
	override val monadScope = ObservableF

	override val possibilities: Int = 10
	override fun factory(possibility: Int): ObservableF<Int> = observableFactory(possibility)

	override fun <A> assertEqualF(r1: Functor<ObservableContext, A>, r2: Functor<ObservableContext, A>) {
		assertEqualObs(r1.asObservable, r2.asObservable)
	}

	@Ignore
	@Test
	override fun `right zero`() {
	}

	@Ignore
	@Test
	override fun `lift2 is correct`() {
	}
}

