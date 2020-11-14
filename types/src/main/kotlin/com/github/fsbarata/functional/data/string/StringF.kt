package com.github.fsbarata.functional.data.string

import com.github.fsbarata.functional.data.Semigroup


data class StringF(val get: String): Semigroup<StringF> {
	override fun combineWith(other: StringF) =
		StringF(get + other.get)

	override fun toString() = get
}
