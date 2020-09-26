package com.fsbarata.fp.types

interface NonEmptyCollection<out A>: NonEmptyIterable<A>, Collection<A> {
	override val head: A
	override val tail: Collection<A>

	@Deprecated("Non empty collection cannot be empty", replaceWith = ReplaceWith("false"))
	override fun isEmpty() = false

	override val size: Int get() = 1 + tail.size

	override fun iterator() = NonEmptyIterator(head, tail.iterator())

	override fun contains(element: @UnsafeVariance A) = head == element || tail.contains(element)
	override fun containsAll(elements: Collection<@UnsafeVariance A>) = elements.all(this::contains)

}