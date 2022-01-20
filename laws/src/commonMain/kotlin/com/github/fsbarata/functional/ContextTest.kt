package com.github.fsbarata.functional

import kotlin.test.asserter

interface ContextTest<F> {
	fun <A> Context<F, A>.equalTo(other: Context<F, A>): Boolean = this == other
	fun <A> Context<F, A>.describe() = toString()

	fun <A> assertEqualF(r1: Context<F, A>, r2: Context<F, A>) {
		asserter.assertTrue({ "${r1.describe()} should be equal to ${r2.describe()}" }, r1.equalTo(r2))
	}
}