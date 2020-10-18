package com.fsbarata.utils.iterators

class LambdaListIterator<A>(
	private val size: Int,
	private var index: Int = 0,
	private val get: (index: Int) -> A,
): ListIterator<A> {
	override fun hasNext() = index < size
	override fun hasPrevious() = index > 0
	override fun next() = get(index++)
	override fun previous() = get(--index)
	override fun nextIndex() = index
	override fun previousIndex() = index - 1
}

fun listEquals(list1: List<*>, list2: List<*>): Boolean {
	if (list1.size != list2.size) return false
	return iteratorEquals(list1.iterator(), list2.iterator())
}

fun iteratorEquals(iterator1: Iterator<*>, iterator2: Iterator<*>): Boolean {
	while (iterator1.hasNext()) {
		if (!iterator2.hasNext() || iterator1.next() != iterator2.next())
			return false
	}
	return !iterator2.hasNext()
}
