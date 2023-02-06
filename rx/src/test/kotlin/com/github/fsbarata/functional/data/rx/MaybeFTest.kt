package com.github.fsbarata.functional.data.rx

import com.github.fsbarata.functional.Context
import com.github.fsbarata.functional.control.MonadZipLaws
import org.junit.Ignore
import org.junit.Test

class MaybeFTest: MonadZipLaws<MaybeContext> {
	override val monadScope = MaybeF
	override fun <A> Context<MaybeContext, A>.equalTo(other: Context<MaybeContext, A>): Boolean {
		val testObserver1 = asMaybe.materialize().test()
		val testObserver2 = other.asMaybe.materialize().test()

		return testObserver2.values() == testObserver1.values()
	}

	override val possibilities: Int = 4
	override fun factory(possibility: Int) = maybeFactory(possibility)

	@Ignore
	@Test
	override fun `scope lift2 is correct`() {
	}

	@Ignore
	@Test
	override fun `lift2 is correct`() {
	}
}
