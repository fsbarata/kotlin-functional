package com.github.fsbarata.functional.data

import com.github.fsbarata.functional.data.list.NonEmptyList

fun <A: Semigroup<A>> NonEmptyList<A>.sconcat() =
	reduce { a1, a2 -> a1.combineWith(a2) }
