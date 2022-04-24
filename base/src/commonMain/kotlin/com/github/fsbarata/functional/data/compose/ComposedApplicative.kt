package com.github.fsbarata.functional.data.compose

import com.github.fsbarata.functional.Context
import com.github.fsbarata.functional.control.Applicative
import com.github.fsbarata.functional.data.Functor

internal typealias ComposeAppContext<F, G> = ComposedApplicative<F, G, *>

typealias ComposedContext<F, G> = ComposedApplicative<F, G, *>

class ComposedApplicative<F, G, A>(
	val underlying: Context<F, Context<G, A>>,
	private val fScope: Applicative.Scope<F>,
	private val gScope: Applicative.Scope<G>,
): Applicative<ComposeAppContext<F, G>, A> {
	override val scope = Scope(fScope, gScope)

	override fun <B> map(f: (A) -> B): ComposedApplicative<F, G, B> =
		fScope.map(underlying) { g -> gScope.map(g, f) }.composed(fScope, gScope)

	override infix fun <B> ap(ff: Context<ComposedContext<F, G>, (A) -> B>): ComposedApplicative<F, G, B> =
		fScope.lift2(ff.asComposed.underlying, underlying) { f, a -> gScope.ap(a, f) }.composed(fScope, gScope)

	override fun <B, R> lift2(fb: Context<ComposedContext<F, G>, B>, f: (A, B) -> R): ComposedApplicative<F, G, R> =
		fScope.lift2(underlying, fb.asComposed.underlying) { a, b -> gScope.lift2(a, b, f) }
			.composed(fScope, gScope)

	override fun equals(other: Any?): Boolean {
		if (this === other) return true
		if (other !is ComposedApplicative<*, *, *>) return false
		return (underlying == other.underlying)
	}

	override fun hashCode() = underlying.hashCode()

	override fun toString() = "Composed($underlying)"

	class Scope<F, G>(
		private val f: Applicative.Scope<F>,
		private val g: Applicative.Scope<G>,
	): Applicative.Scope<ComposedContext<F, G>> {
		override fun <A> just(a: A): ComposedApplicative<F, G, A> =
			ComposedApplicative(f.just(g.just(a)), f, g)
	}
}

fun <F, G, A> Context<F, Context<G, A>>.composed(fScope: Applicative.Scope<F>, gScope: Applicative.Scope<G>) =
	ComposedApplicative(this, fScope, gScope)

fun <F, G, A> Applicative<F, Functor<G, A>>.composed(gScope: Applicative.Scope<G>) =
	ComposedApplicative(this, scope, gScope)

val <F, G, A> Context<ComposeAppContext<F, G>, A>.asComposed
	get() = this as ComposedApplicative<F, G, A>
