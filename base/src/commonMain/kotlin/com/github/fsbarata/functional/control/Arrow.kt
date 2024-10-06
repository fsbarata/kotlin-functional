package com.github.fsbarata.functional.control

interface Arrow<ARR, A, R>: Category<ARR, A, R> {
	override val scope: Scope<ARR>

	override infix fun <RR> composeForward(other: Category<ARR, R, RR>): Arrow<ARR, A, RR> =
		super.composeForward(other) as Arrow

	override infix fun <B> compose(other: Category<ARR, B, A>): Arrow<ARR, B, R> =
		super.compose(other) as Arrow

	fun <PASS> first(): Arrow<ARR, Pair<A, PASS>, Pair<R, PASS>> =
		this split scope.id()

	fun <PASS> second(): Arrow<ARR, Pair<PASS, A>, Pair<PASS, R>> =
		(scope.id<PASS>() as Arrow) split this

	infix fun <B, RR> split(other: Category<ARR, B, RR>): Arrow<ARR, Pair<A, B>, Pair<R, RR>> =
		first<B>() composeForward scope.arr { Pair(it.second, it.first) } composeForward
				((other as Arrow).first<R>() composeForward scope.arr { Pair(it.second, it.first) })

	infix fun <RR> fanout(other: Category<ARR, A, RR>): Arrow<ARR, A, Pair<R, RR>> =
		(scope.arr<A, Pair<A, A>> { Pair(it, it) } as Arrow) composeForward (this split other)

	interface Scope<ARR>: Category.Scope<ARR> {
		fun <A, R> arr(f: (A) -> R): Category<ARR, A, R>
		override fun <B> id(): Category<ARR, B, B> = arr { it }
	}
}
