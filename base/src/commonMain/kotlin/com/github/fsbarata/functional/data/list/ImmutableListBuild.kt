package com.github.fsbarata.functional.data.list

class ImmutableListBuildScope<A>(initialCapacity: Int = -1): MutableList<A>, RandomAccess {
	private var mutableList: MutableList<A>? = if (initialCapacity >= 0) ArrayList(initialCapacity) else ArrayList()
	private val list: List<A>? get() = mutableList

	fun build(): ListF<A> {
		val unreachableList = mutableList ?: throw IllegalStateException("build() can only be called once")
		mutableList = null
		return ListF(unreachableList)
	}

	private fun builtError(): Nothing = throw IllegalStateException("list has been built")
	private fun listOrError(): List<A> = list ?: builtError()

	override val size: Int get() = list?.size ?: 0
	override fun isEmpty(): Boolean = size == 0

	override operator fun get(index: Int): A = listOrError()[index]

	override fun indexOf(element: A): Int = list?.indexOf(element) ?: -1
	override fun lastIndexOf(element: A): Int = list?.lastIndexOf(element) ?: -1

	override fun contains(element: A): Boolean {
		return list?.contains(element) ?: false
	}

	override fun containsAll(elements: Collection<A>): Boolean {
		return list?.containsAll(elements) ?: false
	}

	private var modCount = 0
	private inline fun <R> modifyOrNull(block: MutableList<A>.() -> R): R? {
		val r = mutableList?.block()
		modCount++
		return r
	}

	private inline fun <R> modifyOrError(block: MutableList<A>.() -> R): R {
		val mutableList = mutableList ?: builtError()
		val r = mutableList.block()
		if (this.mutableList == null) builtError()
		modCount++
		return r
	}

	override fun subList(fromIndex: Int, toIndex: Int): MutableList<A> {
		throw UnsupportedOperationException("Operation is not supported within the ImmutableListBuildScope")
	}

	override fun iterator(): MutableIterator<A> = listIterator()
	override fun listIterator(): MutableListIterator<A> = listIterator(0)
	override fun listIterator(index: Int): MutableListIterator<A> = ListIterator(this, 0)

	override operator fun set(index: Int, element: A): A = modifyOrError { set(index, element) }

	override fun add(element: A): Boolean = modifyOrNull { add(element) } ?: false
	override fun add(index: Int, element: A) {
		modifyOrError { add(index, element) }
	}

	override fun addAll(elements: Collection<A>): Boolean = addAll(size, elements)
	override fun addAll(index: Int, elements: Collection<A>): Boolean = when (elements.size) {
		0 -> false
		1 -> modifyOrNull { add(index, elements.first()) } != null
		else -> modifyOrNull { addAll(index, elements) } ?: false
	}

	override fun retainAll(elements: Collection<A>): Boolean = modifyOrNull { retainAll(elements) } ?: false

	override fun remove(element: A): Boolean = modifyOrNull { remove(element) } ?: false
	override fun removeAt(index: Int): A = modifyOrError { removeAt(index) }

	override fun removeAll(elements: Collection<A>): Boolean = modifyOrNull { removeAll(elements) } ?: false

	override fun clear() {
		modifyOrError { clear() }
	}

	private class ListIterator<A>(
		private val mutableList: ImmutableListBuildScope<A>,
		startIndex: Int,
	): MutableListIterator<A> {
		var index = startIndex
		var indexBefore = -1

		private var expectedModCount = mutableList.modCount

		override fun hasPrevious(): Boolean = index > 0 && mutableList.size > 0
		override fun hasNext(): Boolean = index < mutableList.size

		override fun previousIndex(): Int = index - 1
		override fun nextIndex(): Int = index

		override fun previous(): A {
			checkModCount()
			if (!hasPrevious()) throw NoSuchElementException()
			indexBefore = index
			return mutableList[--index]
		}

		override fun next(): A {
			checkModCount()
			if (!hasNext()) throw NoSuchElementException()
			indexBefore = index
			return mutableList[index++]
		}

		override fun add(element: A) {
			checkModCount()
			mutableList.add(index, element)
			updateModCount()
			indexBefore = -1
		}

		override fun remove() {
			checkModCount()
			check(indexBefore != -1) { "Call to previous() or next() required" }
			mutableList.removeAt(indexBefore)
			updateModCount()
			indexBefore = -1
		}

		override fun set(element: A) {
			checkModCount()
			check(indexBefore != -1) { "Call to previous() or next() required" }
			mutableList[indexBefore] = element
			updateModCount()
		}


		fun checkModCount() {
			if (mutableList.modCount != expectedModCount) throw ConcurrentModificationException()
		}

		fun updateModCount() {
			expectedModCount = mutableList.modCount
		}
	}
}

inline fun <A> buildListF(sizeHint: Int = -1, f: ImmutableListBuildScope<A>.() -> Unit): ListF<A> {
	return ImmutableListBuildScope<A>(sizeHint).apply(f).build()
}
