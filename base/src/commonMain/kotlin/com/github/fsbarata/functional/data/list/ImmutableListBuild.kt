package com.github.fsbarata.functional.data.list

class ImmutableListBuildScope<A>(sizeHint: Int = -1) {
	private var list: MutableList<A>? = if (sizeHint >= 0) ArrayList(sizeHint) else ArrayList()

	val size: Int get() = list?.size ?: 0

	fun isEmpty(): Boolean = size == 0

	fun build(): ListF<A> {
		val unreachableList = list ?: throw IllegalStateException("build() can only be called once")
		list = null
		return ListF(unreachableList)
	}

	fun contains(element: A): Boolean {
		return list?.contains(element) ?: false
	}

	fun containsAll(elements: Collection<A>): Boolean {
		return list?.containsAll(elements) ?: false
	}

	fun add(item: A): Boolean {
		return list?.add(item) ?: false
	}

	fun add(index: Int, item: A): Boolean {
		return list?.add(index, item) != null
	}

	fun remove(element: A): Boolean {
		return list?.remove(element) ?: false
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
