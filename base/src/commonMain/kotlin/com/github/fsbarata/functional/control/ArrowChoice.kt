package com.github.fsbarata.functional.control

import com.github.fsbarata.functional.data.either.Either
import com.github.fsbarata.functional.data.id

/**
 * Arrow choice
 * Allows a left or right choice
 * Subtypes must either override left or splitChoice
 */
interface ArrowChoice<ARR, A, R>: Arrow<ARR, A, R> {
	override val scope: Scope<ARR>

	fun <PASS> left(): ArrowChoice<ARR, Either<A, PASS>, Either<R, PASS>> =
		leftFromSplitChoice<ARR, A, R, PASS>(scope, this) as ArrowChoice

	fun <PASS> right(): ArrowChoice<ARR, Either<PASS, A>, Either<PASS, R>> =
		(scope.id<PASS>() as ArrowChoice).splitChoice(this)

	infix fun <B, RR> splitChoice(other: Category<ARR, B, RR>): ArrowChoice<ARR, Either<A, B>, Either<R, RR>> =
		splitChoiceFromLeft(scope, this, other) as ArrowChoice

	infix fun <B> fanin(other: Category<ARR, B, R>): ArrowChoice<ARR, Either<A, B>, R> =
		splitChoice(other).composeForward(scope.arr { it.fold(::id, ::id) }) as ArrowChoice

	interface Scope<ARR>: Arrow.Scope<ARR> {
		fun <A, R, PASS> left(arrow: Category<ARR, A, R>): Category<ARR, Either<A, PASS>, Either<R, PASS>> =
			if (arrow is ArrowChoice) arrow.left()
			else leftFromSplitChoice(this, arrow)

		fun <A, R, PASS> right(arrow: Category<ARR, A, R>): Category<ARR, Either<PASS, A>, Either<PASS, R>> =
			splitChoice(id<PASS>() as ArrowChoice, arrow)

		fun <A, B, R, RR> splitChoice(
			arr1: Category<ARR, A, R>,
			arr2: Category<ARR, B, RR>,
		): Category<ARR, Either<A, B>, Either<R, RR>> =
			if (arr1 is ArrowChoice) arr1.splitChoice(arr2)
			else splitChoiceFromLeft(this, arr1, arr2)

		fun <A, B, R> fanin(arr1: Category<ARR, A, R>, arr2: Category<ARR, B, R>): Category<ARR, Either<A, B>, R> =
			splitChoice(arr1, arr2).composeForward(arr { it.fold(::id, ::id) })
	}
}

fun <ARR, A, R, PASS> leftFromSplitChoice(
	scope: ArrowChoice.Scope<ARR>,
	arrow: Category<ARR, A, R>,
): Category<ARR, Either<A, PASS>, Either<R, PASS>> = scope.splitChoice(arrow, scope.id())

fun <ARR, A, B, R, RR> splitChoiceFromLeft(
	scope: ArrowChoice.Scope<ARR>,
	arr1: Category<ARR, A, R>,
	arr2: Category<ARR, B, RR>
) = with(scope) {
	left<A, R, B>(arr1)
		.composeForward(arr { it.swap() })
		.composeForward(left(arr2))
		.composeForward(arr { it.swap() })
}