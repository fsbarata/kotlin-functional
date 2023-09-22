package com.github.fsbarata.functional.data.list

class ImmutableListBuildScope<A>(sizeHint: Int = -1): MutableCollection<A>, RandomAccess {
	private var list: MutableList<A>? = if (sizeHint >= 0) ArrayList(sizeHint) else ArrayList()

	override val size: Int get() = list?.size ?: 0

	override fun isEmpty(): Boolean = size == 0

	fun build(): ListF<A> {
		val unreachableList = list ?: throw IllegalStateException("build() can only be called once")
		list = null
		return ListF(unreachableList)
	}

	private fun listOrError(): MutableList<A> =
		checkNotNull(list) { "list has been built" }

	operator fun get(index: Int): A = listOrError()[index]

	operator fun set(index: Int, element: A): A =
		listOrError().set(index, element)

	override fun iterator(): MutableIterator<A> {
		val iterator = listOrError().iterator()
		return object: MutableIterator<A> by iterator {
			override fun remove() {
				checkNotNull(list) { "list has been built" }
				iterator.remove()
			}
		}
	}

	fun indexOf(element: A): Int = list?.indexOf(element) ?: -1

	fun lastIndexOf(element: A): Int = list?.lastIndexOf(element) ?: -1

	override fun contains(element: A): Boolean {
		return list?.contains(element) ?: false
	}

	override fun containsAll(elements: Collection<A>): Boolean {
		return list?.containsAll(elements) ?: false
	}

	override fun add(element: A): Boolean {
		return list?.add(element) ?: false
	}

	fun add(index: Int, element: A): Boolean {
		return list?.add(index, element) != null
	}

	override fun remove(element: A): Boolean {
		return list?.remove(element) ?: false
	}

	fun removeAt(index: Int): A =
		listOrError().removeAt(index)

	override fun addAll(elements: Collection<A>): Boolean {
		return addAll(size, elements)
	}

	fun addAll(index: Int, elements: Collection<A>): Boolean {
		return when (elements.size) {
			0 -> false
			1 -> add(index, elements.first())
			else -> list?.addAll(index, elements) ?: false
		}
	}

	override fun retainAll(elements: Collection<A>): Boolean {
		return list?.retainAll(elements) ?: false
	}

	override fun removeAll(elements: Collection<A>): Boolean {
		return list?.removeAll(elements) ?: false
	}

	override fun clear() {
		listOrError().clear()
	}
}

inline fun <A> buildListF(sizeHint: Int = -1, f: ImmutableListBuildScope<A>.() -> Unit): ListF<A> {
	return ImmutableListBuildScope<A>(sizeHint).apply(f).build()
}
