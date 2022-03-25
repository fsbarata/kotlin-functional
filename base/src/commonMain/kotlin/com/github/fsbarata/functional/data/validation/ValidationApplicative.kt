package com.github.fsbarata.functional.data.validation

import com.github.fsbarata.functional.control.*
import com.github.fsbarata.functional.data.Functor
import com.github.fsbarata.functional.data.Semigroup
import com.github.fsbarata.functional.data.list.invoke
import com.github.fsbarata.functional.data.partial


class ValidationApplicativeScope<E: Semigroup<E>>: Applicative.Scope<ValidationContext<E>> {
	override fun <A> just(a: A): Validation<E, A> = Validation.Success(a)

	override fun <A, R> ap(
		app: Functor<ValidationContext<E>, A>,
		ff: Functor<ValidationContext<E>, (A) -> R>,
	): Validation<E, R> = app.asValidation.ap(ff.asValidation)

	override fun <A, B, R> lift2(
		fa: Functor<ValidationContext<E>, A>,
		fb: Functor<ValidationContext<E>, B>,
		f: (A, B) -> R,
	): Validation<E, R> = ap(fb, fa.map { partial(f, it) })
}

fun <E: Semigroup<E>, A, R> Validation<E, A>.ap(ff: Validation<E, (A) -> R>): Validation<E, R> =
	ff.fold(
		ifFailure = { e1 ->
			Validation.Failure(fold(
				ifFailure = { e1.combineWith(it) },
				ifSuccess = { e1 }
			))
		},
		ifSuccess = { f -> map { f(it) } }
	)

inline fun <E: Semigroup<E>, A, B, R> lift2(fa: Validation<E, A>, fb: Validation<E, B>, f: (A, B) -> R) =
	fb.ap(fa.map { f.partial(it) })

operator fun <E: Semigroup<E>, A, B, R> Lift2<A, B, R>.invoke(
	v1: Validation<E, A>,
	v2: Validation<E, B>,
) = lift2(v1, v2, f)

operator fun <E: Semigroup<E>, A, B, C, R> Lift3<A, B, C, R>.invoke(
	v1: Validation<E, A>,
	v2: Validation<E, B>,
	v3: Validation<E, C>,
) = app(ValidationApplicativeScope(), v1, v2, v3).asValidation

operator fun <E: Semigroup<E>, A, B, C, D, R> Lift4<A, B, C, D, R>.invoke(
	v1: Validation<E, A>,
	v2: Validation<E, B>,
	v3: Validation<E, C>,
	v4: Validation<E, D>,
) = app(ValidationApplicativeScope(), v1, v2, v3, v4).asValidation


fun <E: Semigroup<E>, A, B, R> lift2Valid(f: (A, B) -> R): (Validation<E, A>, Validation<E, B>) -> Validation<E, R> = lift2(f)::invoke
fun <E: Semigroup<E>, A, B, C, R> lift3Valid(f: (A, B, C) -> R): (Validation<E, A>, Validation<E, B>, Validation<E, C>) -> Validation<E, R> = lift3(f)::invoke
