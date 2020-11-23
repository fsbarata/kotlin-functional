package com.github.fsbarata.functional.data.compose

import com.github.fsbarata.functional.control.Applicative

class ComposedApplicative<F, G, A>(
	override val underlying: Applicative<F, Applicative<G, A>>,
	private val gScope: Applicative.Scope<G>,
): Applicative<ComposeContext<F, G>, A>,
	Composed<F, G, A>(underlying) {
	override val scope = Scope(underlying.scope, gScope)

	override fun <B> map(f: (A) -> B): ComposedApplicative<F, G, B> =
		underlying.map { g -> g.map(f) }.compose(gScope)

	override infix fun <B> ap(ff: Applicative<ComposeContext<F, G>, (A) -> B>): ComposedApplicative<F, G, B> =
		ff.asCompose.underlying.lift2(underlying) { f, a -> a.ap(f) }.compose(gScope)

	override fun <B, R> lift2(fb: Applicative<ComposeContext<F, G>, B>, f: (A, B) -> R): ComposedApplicative<F, G, R> =
		underlying.lift2(fb.asCompose.underlying) { a, b -> a.lift2(b, f) }.compose(gScope)

	class Scope<F, G>(
		private val f: Applicative.Scope<F>,
		private val g: Applicative.Scope<G>,
	): Applicative.Scope<ComposeContext<F, G>> {
		override fun <A> just(a: A): ComposedApplicative<F, G, A> = ComposedApplicative(f.just(g.just(a)), g)
	}
}

fun <F, G, A> Applicative<F, Applicative<G, A>>.compose(gScope: Applicative.Scope<G>) =
	ComposedApplicative(this, gScope)

val <F, G, A> Applicative<ComposeContext<F, G>, A>.asCompose
	get() = this as ComposedApplicative<F, G, A>
