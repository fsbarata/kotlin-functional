package com.github.fsbarata.functional.data.validation

import com.github.fsbarata.functional.control.Applicative
import com.github.fsbarata.functional.control.Lift2
import com.github.fsbarata.functional.control.Lift3
import com.github.fsbarata.functional.control.Lift4
import com.github.fsbarata.functional.data.Semigroup
import com.github.fsbarata.functional.data.partial


class ValidationApplicative<E: Semigroup<E>, A>(
	private val v: Validation<E, A>,
): Applicative<ValidationContext<@UnsafeVariance E>, A> {
	override val scope = Scope<E>()

	fun unwrap() = v

	@Suppress("OVERRIDE_BY_INLINE")
	override inline fun <B> map(f: (A) -> B) = unwrap().map(f).toApplicative()

	override fun <R> ap(ff: Applicative<ValidationContext<E>, (A) -> R>): ValidationApplicative<E, R> =
		v.ap(ff.asValidation.v).toApplicative()

	override fun <B, R> lift2(
		fb: Applicative<ValidationContext<E>, B>,
		f: (A, B) -> R,
	) = lift2(v, fb.asValidation.v, f).toApplicative()

	class Scope<E: Semigroup<E>>: Applicative.Scope<ValidationContext<E>> {
		override fun <A> just(a: A) = Validation.Success(a).toApplicative<E, A>()
	}
}

fun <E: Semigroup<E>, A> Validation<E, A>.toApplicative() = ValidationApplicative(this)

val <E: Semigroup<E>, A> Applicative<ValidationContext<E>, A>.asValidation
	get() = this as ValidationApplicative<E, A>

fun <E: Semigroup<E>, A, R> Validation<E, A>.ap(ff: Validation<E, (A) -> R>) =
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

inline fun <E: Semigroup<E>, A, B, C, R> lift3(
	fa: Validation<E, A>, fb: Validation<E, B>, fc: Validation<E, C>,
	f: (A, B, C) -> R,
) = fc.ap(lift2(fa, fb) { a, b -> f.partial(a, b) })

inline fun <E: Semigroup<E>, A, B, C, D, R> lift4(
	fa: Validation<E, A>, fb: Validation<E, B>, fc: Validation<E, C>, fd: Validation<E, D>,
	f: (A, B, C, D) -> R,
): Validation<E, R> = fd.ap(lift3(fa, fb, fc) { a, b, c -> f.partial(a, b, c) })

operator fun <E: Semigroup<E>, A, B, R> Lift2<A, B, R>.invoke(
	v1: Validation<E, A>,
	v2: Validation<E, B>
) = lift2(v1, v2, f)

operator fun <E: Semigroup<E>, A, B, C, R> Lift3<A, B, C, R>.invoke(
	v1: Validation<E, A>,
	v2: Validation<E, B>,
	v3: Validation<E, C>,
) = lift3(v1, v2, v3, f)

operator fun <E: Semigroup<E>, A, B, C, D, R> Lift4<A, B, C, D, R>.invoke(
	v1: Validation<E, A>,
	v2: Validation<E, B>,
	v3: Validation<E, C>,
	v4: Validation<E, D>,
) = lift4(v1, v2, v3, v4, f)
