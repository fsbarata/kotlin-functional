package com.github.fsbarata.functional.data


data class StringF(val get: String): Semigroup<StringF> {
	override fun combineWith(other: StringF) =
		StringF(get + other.get)

	override fun toString() = get
}
