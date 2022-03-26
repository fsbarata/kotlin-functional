package com.github.fsbarata.functional.control

import com.github.fsbarata.functional.data.Functor
import com.github.fsbarata.functional.data.partial2
import com.github.fsbarata.functional.data.partial3

fun <A, R> lift(f: (A) -> R) = Lift1(f)

class Lift1<A, R>(val f: (A) -> R) {
	fun <F> fmap(fa: Functor<F, A>): Functor<F, R> = fa.map(f)
}

fun <A, B, R> lift2(f: (A, B) -> R) = Lift2(f)

class Lift2<A, B, R>(val f: (A, B) -> R) {
	fun <F> app(fa: Applicative<F, A>, fb: Applicative<F, B>): Applicative<F, R> =
		fa.lift2(fb, f)

	fun <F> app(scope: Applicative.Scope<F>, fa: Functor<F, A>, fb: Functor<F, B>): Functor<F, R> =
		scope.lift2(fa, fb, f)
}

fun <A, B, C, R> lift3(f: (A, B, C) -> R) = Lift3(f)

class Lift3<A, B, C, R>(val f: (A, B, C) -> R) {
	fun <F> app(
		fa: Applicative<F, A>,
		fb: Applicative<F, B>,
		fc: Applicative<F, C>,
	): Applicative<F, R> =
		fc.ap(lift2(f::partial2).app(fa, fb))

	fun <F> app(
		scope: Applicative.Scope<F>,
		fa: Functor<F, A>,
		fb: Functor<F, B>,
		fc: Functor<F, C>,
	): Functor<F, R> =
		scope.ap(fc, scope.lift2(fa, fb, f::partial2))
}

fun <A, B, C, D, R> lift4(f: (A, B, C, D) -> R) = Lift4(f)

class Lift4<A, B, C, D, R>(val f: (A, B, C, D) -> R) {
	fun <F> app(
		fa: Applicative<F, A>,
		fb: Applicative<F, B>,
		fc: Applicative<F, C>,
		fd: Applicative<F, D>,
	): Applicative<F, R> =
		fd.ap(lift3(f::partial3).app(fa, fb, fc))

	fun <F> app(
		scope: Applicative.Scope<F>,
		fa: Functor<F, A>,
		fb: Functor<F, B>,
		fc: Functor<F, C>,
		fd: Functor<F, D>,
	): Functor<F, R> =
		scope.ap(fd, lift3(f::partial3).app(scope, fa, fb, fc))
}

