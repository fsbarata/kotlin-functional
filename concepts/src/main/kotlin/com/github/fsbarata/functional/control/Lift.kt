package com.github.fsbarata.functional.control

import com.github.fsbarata.functional.data.partial

fun <A, B, R> lift2(f: (A, B) -> R) = Lift2(f)

class Lift2<A, B, R>(private val f: (A, B) -> R) {
	operator fun <F> invoke(fa: Applicative<F, A>, fb: Applicative<F, B>): Applicative<F, R> =
		fa.lift2(fb, f)
}

fun <A, B, C, R> lift3(f: (A, B, C) -> R) = Lift3(f)

class Lift3<A, B, C, R>(private val f: (A, B, C) -> R) {
	operator fun <F> invoke(
		fa: Applicative<F, A>,
		fb: Applicative<F, B>,
		fc: Applicative<F, C>,
	): Applicative<F, R> =
		fc.ap(lift2(f::partial)(fa, fb))
}

fun <A, B, C, D, R> lift4(f: (A, B, C, D) -> R) = Lift4(f)

class Lift4<A, B, C, D, R>(private val f: (A, B, C, D) -> R) {
	operator fun <F> invoke(
		fa: Applicative<F, A>,
		fb: Applicative<F, B>,
		fc: Applicative<F, C>,
		fd: Applicative<F, D>,
	): Applicative<F, R> =
		fd.ap(lift3(f::partial)(fa, fb, fc))
}

