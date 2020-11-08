package com.github.fsbarata.functional.data.compose

import com.github.fsbarata.functional.control.test.FunctorTest
import com.github.fsbarata.functional.data.list.NonEmptyContext
import com.github.fsbarata.functional.data.list.NonEmptyList
import com.github.fsbarata.functional.data.validation.Validation
import com.github.fsbarata.functional.data.validation.ValidationContext

class ComposeTest: FunctorTest<ComposeContext<NonEmptyContext, ValidationContext>> {
	override fun <A> createFunctor(a: A) =
		Compose(NonEmptyList.just(Validation.Success(a)))
}