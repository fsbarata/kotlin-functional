package com.github.fsbarata.functional.data.list

class ImmutableListBuildScope<A>(sizeHint: Int = -1): RandomAccess {
	private var list: MutableList<A>? = if (sizeHint >= 0) ArrayList(sizeHint) else ArrayList()

	val size: Int get() = list?.size ?: 0

	fun isEmpty(): Boolean = size == 0

	fun build(): ListF<A> {
		val unreachableList = list ?: throw IllegalStateException("build() can only be called once")
		list = null
		return ListF(unreachableList)
	}

	operator fun get(index: Int): A =
		list?.get(index) ?: throw IllegalStateException("list has been built")

	operator fun set(index: Int, element: A): A =
		list?.set(index, element) ?: throw IllegalStateException("list has been built")

	fun iterator(): Iterator<A> {
		val iterator = list?.iterator() ?: throw IllegalStateException("list has been built")
		return object : Iterator<A> by iterator {}
	}

	fun indexOf(element: A): Int = list?.indexOf(element) ?: -1

	fun lastIndexOf(element: A): Int = list?.lastIndexOf(element) ?: -1

	fun contains(element: A): Boolean {
		return list?.contains(element) ?: false
	}

	fun containsAll(elements: Collection<A>): Boolean {
		return list?.containsAll(elements) ?: false
	}

	fun add(element: A): Boolean {
		return list?.add(element) ?: false
	}

	fun add(index: Int, element: A): Boolean {
		return list?.add(index, element) != null
	}

	fun remove(element: A): Boolean {
		return list?.remove(element) ?: false
	}

	fun removeAt(index: Int): A =
		list?.removeAt(index) ?: throw IllegalStateException("list has been built")

	fun addAll(elements: Iterable<A>): Boolean {
		return when (elements) {
			is Collection -> addAll(size, elements)
			else -> elements.all { list?.add(it) ?: false }
		}
	}

	fun addAll(elements: Collection<A>): Boolean {
		return addAll(size, elements)
	}

	fun addAll(index: Int, elements: Collection<A>): Boolean {
		return when (elements.size) {
			0 -> false
			1 -> add(index, elements.first())
			else -> list?.addAll(index, elements) ?: false
		}
	}

	fun retainAll(elements: Collection<A>): Boolean {
		return list?.retainAll(elements) ?: false
	}

	fun removeAll(elements: Collection<A>): Boolean {
		return list?.removeAll(elements) ?: false
	}

	fun clear() {
		list?.clear()
	}
}

inline fun <A> buildListF(sizeHint: Int = -1, f: ImmutableListBuildScope<A>.() -> Unit): ListF<A> {
	return ImmutableListBuildScope<A>(sizeHint).apply(f).build()
}
