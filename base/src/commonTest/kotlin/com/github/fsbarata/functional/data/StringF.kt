package com.github.fsbarata.functional.data


data class StringF(val get: String): Semigroup<StringF> {
	override fun concatWith(other: StringF) =
		StringF(get + other.get)

	override fun toString() = get
}
