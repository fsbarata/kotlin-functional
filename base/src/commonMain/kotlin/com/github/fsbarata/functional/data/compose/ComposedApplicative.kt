package com.github.fsbarata.functional.data.compose

import com.github.fsbarata.functional.Context
import com.github.fsbarata.functional.control.Applicative
import com.github.fsbarata.functional.data.Functor

class ComposedApplicative<F, G, A>(
	override val underlying: Functor<F, Functor<G, A>>,
	private val fScope: Applicative.Scope<F>,
	private val gScope: Applicative.Scope<G>,
): Applicative<ComposeContext<F, G>, A>,
	Composed<F, G, A>(underlying) {
	override val scope = Scope(fScope, gScope)

	override fun <B> map(f: (A) -> B): ComposedApplicative<F, G, B> =
		underlying.map { g -> g.map(f) }.compose(fScope, gScope)

	override infix fun <B> ap(ff: Functor<ComposeContext<F, G>, (A) -> B>): ComposedApplicative<F, G, B> =
		fScope.lift2(ff.asCompose.underlying, underlying) { f, a -> gScope.ap(a, f) }.compose(fScope, gScope)

	override fun <B, R> lift2(fb: Functor<ComposeContext<F, G>, B>, f: (A, B) -> R): ComposedApplicative<F, G, R> =
		fScope.lift2(underlying, fb.asCompose.underlying) { a, b -> gScope.lift2(a, b, f) }.compose(fScope, gScope)

	class Scope<F, G>(
		private val f: Applicative.Scope<F>,
		private val g: Applicative.Scope<G>,
	): Applicative.Scope<ComposeContext<F, G>> {
		override fun <A> just(a: A): ComposedApplicative<F, G, A> = ComposedApplicative(f.just(g.just(a)), f, g)
	}
}

fun <F, G, A> Functor<F, Functor<G, A>>.compose(fScope: Applicative.Scope<F>, gScope: Applicative.Scope<G>) =
	ComposedApplicative(this, fScope, gScope)

fun <F, G, A> Applicative<F, Functor<G, A>>.compose(gScope: Applicative.Scope<G>) =
	ComposedApplicative(this, scope, gScope)

val <F, G, A> Context<ComposeContext<F, G>, A>.asCompose
	get() = this as ComposedApplicative<F, G, A>
