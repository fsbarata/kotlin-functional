package com.github.fsbarata.functional.data.compose

import com.github.fsbarata.functional.control.ApplicativeLaws
import com.github.fsbarata.functional.data.list.ListContext
import com.github.fsbarata.functional.data.list.ListF
import com.github.fsbarata.functional.data.list.NonEmptyContext
import com.github.fsbarata.functional.data.list.NonEmptyList

class ComposeApplicativeTest: ApplicativeLaws<ComposeContext<ListContext, NonEmptyContext>> {
	override val applicativeScope = ComposeApplicative.Scope(ListF, NonEmptyList)
}