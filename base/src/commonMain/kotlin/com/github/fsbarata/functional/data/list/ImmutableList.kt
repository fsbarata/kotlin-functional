package com.github.fsbarata.functional.data.list

internal interface ImmutableList<out A>: List<A> {
	override fun subList(fromIndex: Int, toIndex: Int): ListF<A>

	fun drop(count: Int): ListF<A> =
		if (count >= size) ListF.empty()
		else subList(count, size)

	fun dropLast(count: Int): ListF<A> =
		if (count >= size) ListF.empty()
		else subList(0, (size - count))

	fun take(count: Int): ListF<A> =
		if (count >= size) ListF.fromList(this)
		else subList(0, count)

	fun takeLast(count: Int): ListF<A> =
		if (count >= size) ListF.fromList(this)
		else subList(size - count, size)

	fun slice(indices: ClosedRange<Int>): ListF<A> =
		if (indices.isEmpty()) ListF.empty()
		else subList(indices.start, indices.endInclusive + 1)

	fun slice(indices: ImmutableList<Int>): ListF<A> = ListF.fromList(SlicedList(this, indices))
}

internal class SlicedList<out A>(
	private val list: ImmutableList<A>,
	private val indices: ImmutableList<Int>,
): ImmutableList<A> {
	override val size: Int = indices.size
	override fun isEmpty() = indices.isEmpty()

	override fun subList(fromIndex: Int, toIndex: Int): ListF<A> =
		ListF.fromList(SlicedList(list, indices.subList(fromIndex, toIndex)))

	override fun iterator(): Iterator<A> = object: Iterator<A> {
		private val iterator = indices.iterator()
		override fun hasNext(): Boolean = iterator.hasNext()
		override fun next(): A = list[iterator.next()]
	}

	override fun contains(element: @UnsafeVariance A): Boolean = indexOf(element) >= 0

	override fun containsAll(elements: Collection<@UnsafeVariance A>): Boolean = elements.all(::contains)

	override fun get(index: Int) = list[indices[index]]

	override fun indexOf(element: @UnsafeVariance A): Int =
		indices.indexOfFirst { list[it] == element }

	override fun lastIndexOf(element: @UnsafeVariance A): Int =
		indices.indexOfLast { list[it] == element }

	override fun listIterator(): ListIterator<A> = object: ListIterator<A> {
		private val iterator = indices.listIterator()
		override fun nextIndex(): Int = iterator.nextIndex()
		override fun previousIndex(): Int = iterator.previousIndex()
		override fun hasNext(): Boolean = iterator.hasNext()
		override fun hasPrevious(): Boolean = iterator.hasPrevious()
		override fun next(): A = list[iterator.next()]
		override fun previous(): A = list[iterator.previous()]
	}

	override fun listIterator(index: Int): ListIterator<A> = object: ListIterator<A> {
		private val iterator = indices.listIterator(index)
		override fun nextIndex(): Int = iterator.nextIndex()
		override fun previousIndex(): Int = iterator.previousIndex()
		override fun hasNext(): Boolean = iterator.hasNext()
		override fun hasPrevious(): Boolean = iterator.hasPrevious()
		override fun next(): A = list[iterator.next()]
		override fun previous(): A = list[iterator.previous()]
	}
}

