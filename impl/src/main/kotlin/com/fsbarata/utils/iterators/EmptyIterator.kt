package com.fsbarata.utils.iterators

object EmptyIterator: Iterator<Nothing> {
	@Deprecated("Empty iterator has no next", replaceWith = ReplaceWith("false"))
	override fun hasNext(): Boolean = false
	@Deprecated("Empty iterator has no next", replaceWith = ReplaceWith("throw NoSuchElementException()"))
	override fun next(): Nothing = throw NoSuchElementException()
}
