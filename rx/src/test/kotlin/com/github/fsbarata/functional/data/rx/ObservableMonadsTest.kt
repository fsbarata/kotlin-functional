package com.github.fsbarata.functional.data.rx

import com.github.fsbarata.functional.Context
import com.github.fsbarata.functional.control.MonadPlusScopeLaws
import com.github.fsbarata.functional.control.MonadZipScopeLaws
import io.reactivex.rxjava3.core.Observable
import org.junit.Ignore
import org.junit.Test

class ObservableSwitchMonadTest:
	MonadPlusScopeLaws<ObservableContext>,
	MonadZipScopeLaws<ObservableContext> {
	override val monadScope = ObservableSwitchMonad

	override val possibilities: Int = 10
	override fun factory(possibility: Int): ObservableF<Int> = observableFactory(possibility)

	override fun <A> assertEqualF(r1: Context<ObservableContext, A>, r2: Context<ObservableContext, A>) {
		assertEqualObs(r1.asObservable, r2.asObservable)
	}

	@Ignore
	@Test
	override fun `scope lift2 is correct`() {
	}
}

class ObservableConcatMonadTest:
	MonadPlusScopeLaws<ObservableContext>,
	MonadZipScopeLaws<ObservableContext> {
	override val monadScope = ObservableConcatMonad

	override val possibilities: Int = 10
	override fun factory(possibility: Int): ObservableF<Int> = observableFactory(possibility)

	override fun <A> assertEqualF(r1: Context<ObservableContext, A>, r2: Context<ObservableContext, A>) {
		assertEqualObs(r1.asObservable, r2.asObservable)
	}

	@Test
	fun klei() {
		assertEqualF(
			Observable.just(6, 16).f(),
			Observable.just(3)
				.flatMap(
					ObservableConcatMonad.kleisli { it: Int -> Observable.just(it, it + 5) }
						.composeForwardKleisli { it: Int -> ObservableF.just(it * 2) }
				)
				.f(),
		)
	}
}

