package com.github.fsbarata.functional.control

import com.github.fsbarata.functional.data.either.Either
import com.github.fsbarata.functional.data.id

interface ArrowChoice<ARR, A, R>: Arrow<ARR, A, R> {
	fun <PASS> left(): Arrow<ARR, Either<A, PASS>, Either<R, PASS>> =
		splitChoice(scope.id<PASS>() as ArrowChoice<ARR, PASS, PASS>)

	fun <PASS> right(): Arrow<ARR, Either<PASS, A>, Either<PASS, R>> =
		(scope.id<PASS>() as ArrowChoice<ARR, PASS, PASS>).splitChoice(this)

	infix fun <B, RR> splitChoice(other: ArrowChoice<ARR, B, RR>): Arrow<ARR, Either<A, B>, Either<R, RR>> =
		left<B>()
			.composeForward(scope.arr { it.swap() })
			.composeForward(other.left())
			.composeForward(scope.arr { it.swap() })

	infix fun <B> fanin(other: ArrowChoice<ARR, B, R>): Arrow<ARR, Either<A, B>, R> =
		splitChoice(other).composeForward(scope.arr { it.fold(::id, ::id) })
}
