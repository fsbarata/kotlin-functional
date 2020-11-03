package com.github.fsbarata.functional.control

interface Arrow<A, B, C>: Category<A, B, C> {
	override val scope: Scope<A>

	override infix fun <D> compose(other: Category<A, C, D>): Arrow<A, B, D>
	override infix fun <D> composeRight(other: Category<A, D, B>): Arrow<A, D, C> =
		super.composeRight(other) as Arrow<A, D, C>

	fun <D> first(): Arrow<A, Pair<B, D>, Pair<C, D>> =
		this split scope.id()

	fun <D> second(): Arrow<A, Pair<D, B>, Pair<D, C>> =
		scope.id<D>() split this

	infix fun <D, E> split(other: Arrow<A, D, E>): Arrow<A, Pair<B, D>, Pair<C, E>> =
		first<D>() compose scope.arr { Pair(it.second, it.first) } compose
				(other.first<C>() compose scope.arr { Pair(it.second, it.first) })

	infix fun <D> fanout(other: Arrow<A, B, D>): Arrow<A, B, Pair<C, D>> =
		scope.arr<B, Pair<B, B>> { Pair(it, it) } compose (this split other)

	interface Scope<A>: Category.Scope<A> {
		fun <B, C> arr(f: (B) -> C): Arrow<A, B, C>
		override fun <B> id(): Arrow<A, B, B> = arr { it }
	}
}
