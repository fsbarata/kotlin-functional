package com.github.fsbarata.functional.control

interface Category<CAT, A, B> {
	val scope: Scope<CAT>

	infix fun <C> compose(other: Category<CAT, B, C>): Category<CAT, A, C>
	infix fun <C> composeRight(other: Category<CAT, C, A>): Category<CAT, C, B> = other.compose(this)

	interface Scope<CAT> {
		fun <A> id(): Category<CAT, A, A>
	}
}
