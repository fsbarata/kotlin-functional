package com.github.fsbarata.functional.data.tuple

import com.github.fsbarata.functional.control.Applicative
import com.github.fsbarata.functional.data.Monoid
import com.github.fsbarata.functional.data.Traversable

data class Tuple2<X, Y>(
	val x: X,
	val y: Y,
): Traversable<Tuple2Context<X>, Y> {
	override val scope get() = Scope<X>()

	override fun <B> map(f: (Y) -> B) =
		Tuple2(x, f(y))

	override fun <F, B> traverse(
		appScope: Applicative.Scope<F>,
		f: (Y) -> Applicative<F, B>,
	) = f(y).map { Tuple2(x, it) }

	override fun <R> foldL(initialValue: R, accumulator: (R, Y) -> R): R =
		accumulator(initialValue, y)

	override fun <R> foldR(initialValue: R, accumulator: (Y, R) -> R): R =
		accumulator(y, initialValue)

	override fun <M> foldMap(monoid: Monoid<M>, f: (Y) -> M): M = f(y)

	override fun toString(): String = "($x, $y)"

	class Scope<X>: Traversable.Scope<Tuple2Context<X>>
}

internal typealias Tuple2Context<X> = Tuple2<X, *>

fun <A, B> Pair<A, B>.f() = Tuple2(first, second)
fun <X, Y> Tuple2<X, Y>.toPair() = Pair(x, y)
