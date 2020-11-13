package com.github.fsbarata.functional.data.validation

import com.github.fsbarata.functional.data.Semigroup
import com.github.fsbarata.functional.data.curry2
import com.github.fsbarata.functional.data.partial


class ValidationApplicative<E>(
	private val semigroup: Semigroup<E>,
) {
	fun <A, R> ap(
		v: Validation<E, A>,
		vf: Validation<E, (A) -> R>,
	): Validation<E, R> = vf.fold(
		ifFailure = { e1 ->
			Validation.Failure(v.fold(
				ifFailure = { semigroup.combine(e1, it) },
				ifSuccess = { e1 }
			))
		},
		ifSuccess = { f -> v.map { f(it) } }
	)

	fun <A, B, R> lift2(
		v1: Validation<E, A>,
		v2: Validation<E, B>,
		f: (A, B) -> R,
	) = ap(v2, v1.map { f.partial(it) })

	fun <A, B, C, R> lift3(
		v1: Validation<E, A>,
		v2: Validation<E, B>,
		v3: Validation<E, C>,
		f: (A, B, C) -> R,
	) = ap(v3, lift2(v1, v2, f.curry2()))
}
