package com.github.fsbarata.functional.control

interface Alternative<C, out A>: Applicative<C, A> {
	override val scope: Scope<C>

	override fun <B> map(f: (A) -> B): Alternative<C, B> =
		super.map(f) as Alternative<C, B>

	override fun <B, R> lift2(fb: Applicative<C, B>, f: (A, B) -> R) =
		super.lift2(fb, f) as Alternative<C, R>

	fun associateWith(other: Alternative<C, @UnsafeVariance A>): Alternative<C, A>

	interface Scope<C>: Applicative.Scope<C> {
		fun <A> empty(): Alternative<C, A>
		override fun <A> just(a: A): Alternative<C, A>
	}
}

fun <C, A> associate(alt1: Alternative<C, A>, alt2: Alternative<C, A>) =
	alt1.associateWith(alt2)
