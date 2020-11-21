package com.github.fsbarata.functional.data.compose

import com.github.fsbarata.functional.data.FunctorLaws
import com.github.fsbarata.functional.data.list.NonEmptyContext
import com.github.fsbarata.functional.data.list.createNel
import com.github.fsbarata.functional.data.validation.Validation
import com.github.fsbarata.functional.data.validation.ValidationContext
import java.io.IOException

class ComposeTest: FunctorLaws<ComposeContext<NonEmptyContext, ValidationContext<Nothing>>> {
	override fun factory(possibility: Int) =
		Compose(
			createNel(possibility)
				.map {
					if (it > 0) Validation.Success(it)
					else Validation.Failure(IOException()) as Validation<Nothing, Int>
				}
		)
}
