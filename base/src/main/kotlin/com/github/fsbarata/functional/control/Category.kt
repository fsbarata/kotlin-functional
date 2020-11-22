package com.github.fsbarata.functional.control

interface Category<CAT, A, B> {
	val scope: Scope<CAT>

	infix fun <C> composeForward(other: Category<CAT, B, C>): Category<CAT, A, C> = other.compose(this)
	infix fun <C> compose(other: Category<CAT, C, A>): Category<CAT, C, B> = other.composeForward(this)

	interface Scope<CAT> {
		fun <A> id(): Category<CAT, A, A>
	}
}
