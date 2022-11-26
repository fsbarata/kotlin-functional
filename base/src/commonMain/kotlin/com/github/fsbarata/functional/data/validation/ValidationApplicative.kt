@file:Suppress("OVERRIDE_BY_INLINE")

package com.github.fsbarata.functional.data.validation

import com.github.fsbarata.functional.Context
import com.github.fsbarata.functional.control.*
import com.github.fsbarata.functional.data.Semigroup
import com.github.fsbarata.functional.data.partial
import com.github.fsbarata.functional.data.semigroupScopeOf


class ValidationApplicativeScope<E>(val semigroupScope: Semigroup.Scope<E>):
	Applicative.Scope<ValidationContext<E>> {
	override fun <A> just(a: A): Validation<E, A> = Validation.Success(a)

	override fun <A, R> ap(
		fa: Context<ValidationContext<E>, A>,
		ff: Context<ValidationContext<E>, (A) -> R>,
	): Validation<E, R> = fa.asValidation.ap(semigroupScope, ff.asValidation)

	override inline fun <A, B, R> lift2(
		fa: Context<ValidationContext<E>, A>,
		fb: Context<ValidationContext<E>, B>,
		f: (A, B) -> R,
	): Validation<E, R> = lift2(fa.asValidation, fb.asValidation, semigroupScope, f)
}

inline fun <E: Semigroup<E>, A, R> Validation<E, A>.ap(ff: Validation<E, (A) -> R>): Validation<E, R> =
	ap(semigroupScopeOf(), ff)

inline fun <E, A, R> Validation<E, A>.ap(semigroupScope: Semigroup.Scope<E>, ff: Validation<E, (A) -> R>): Validation<E, R> =
	ff.fold(
		ifFailure = { e1 ->
			Validation.Failure(fold(
				ifFailure = { semigroupScope.concat(e1, it) },
				ifSuccess = { e1 }
			))
		},
		ifSuccess = { f -> map { f(it) } }
	)

inline fun <E: Semigroup<E>, A, B, R> lift2(fa: Validation<E, A>, fb: Validation<E, B>, f: (A, B) -> R) =
	lift2(fa, fb, semigroupScopeOf(), f)

inline fun <E, A, B, R> lift2(
	fa: Validation<E, A>,
	fb: Validation<E, B>,
	semigroupScope: Semigroup.Scope<E>,
	f: (A, B) -> R,
) =
	fa.fold(
		ifFailure = { ea ->
			Validation.Failure(fb.fold(
				ifFailure = { eb -> semigroupScope.concat(ea, eb) },
				ifSuccess = { ea }
			))
		},
		ifSuccess = { a -> fb.map { f(a, it) } }
	)

operator fun <E: Semigroup<E>, A, B, R> Lift2<A, B, R>.invoke(
	v1: Validation<E, A>,
	v2: Validation<E, B>,
) = lift2(v1, v2, f)

operator fun <E: Semigroup<E>, A, B, C, R> Lift3<A, B, C, R>.invoke(
	v1: Validation<E, A>,
	v2: Validation<E, B>,
	v3: Validation<E, C>,
) = app(Validation.applicative(), v1, v2, v3).asValidation

operator fun <E: Semigroup<E>, A, B, C, D, R> Lift4<A, B, C, D, R>.invoke(
	v1: Validation<E, A>,
	v2: Validation<E, B>,
	v3: Validation<E, C>,
	v4: Validation<E, D>,
) = app(Validation.applicative(), v1, v2, v3, v4).asValidation


fun <E: Semigroup<E>, A, B, R> liftValid2(f: (A, B) -> R): (Validation<E, A>, Validation<E, B>) -> Validation<E, R> =
	lift2(f)::invoke

fun <E: Semigroup<E>, A, B, C, R> liftValid3(f: (A, B, C) -> R): (Validation<E, A>, Validation<E, B>, Validation<E, C>) -> Validation<E, R> =
	lift3(f)::invoke
