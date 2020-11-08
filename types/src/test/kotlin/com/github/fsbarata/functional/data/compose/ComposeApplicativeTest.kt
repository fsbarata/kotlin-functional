package com.github.fsbarata.functional.data.compose

import com.github.fsbarata.functional.control.test.ApplicativeTest
import com.github.fsbarata.functional.data.list.ListContext
import com.github.fsbarata.functional.data.list.ListF
import com.github.fsbarata.functional.data.list.NonEmptyContext
import com.github.fsbarata.functional.data.list.NonEmptyList

class ComposeApplicativeTest: ApplicativeTest<ComposeContext<ListContext, NonEmptyContext>> {
	override val applicativeScope = ComposeApplicative.Scope(ListF, NonEmptyList)
}