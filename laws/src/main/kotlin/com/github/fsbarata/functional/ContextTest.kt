package com.github.fsbarata.functional

interface ContextTest<F> {
	fun <A> Context<F, A>.equalTo(other: Context<F, A>): Boolean = this == other
	fun <A> Context<F, A>.describe() = toString()

	fun <A> assertEqualF(r1: Context<F, A>, r2: Context<F, A>) {
		assert(r1.equalTo(r2)) { "${r1.describe()} should be equal to ${r2.describe()}" }
	}
}