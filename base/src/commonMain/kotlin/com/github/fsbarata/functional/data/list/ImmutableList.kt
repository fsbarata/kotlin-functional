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
}
