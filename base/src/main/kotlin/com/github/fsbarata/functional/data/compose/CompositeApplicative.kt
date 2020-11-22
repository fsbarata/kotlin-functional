package com.github.fsbarata.functional.data.compose

import com.github.fsbarata.functional.control.Applicative

class CompositeApplicative<F, G, A>(
	override val fg: Applicative<F, Applicative<G, A>>,
	private val gScope: Applicative.Scope<G>,
): Applicative<ComposeContext<F, G>, A>,
	Composite<F, G, A>(fg) {
	override val scope = Scope(fg.scope, gScope)

	override fun <B> map(f: (A) -> B): CompositeApplicative<F, G, B> =
		fg.map { g -> g.map(f) }.composite(gScope)

	override infix fun <B> ap(ff: Applicative<ComposeContext<F, G>, (A) -> B>): CompositeApplicative<F, G, B> =
		ff.asCompose.fg.lift2(fg) { f, a -> a.ap(f) }.composite(gScope)

	override fun <B, R> lift2(fb: Applicative<ComposeContext<F, G>, B>, f: (A, B) -> R): CompositeApplicative<F, G, R> =
		fg.lift2(fb.asCompose.fg) { a, b -> a.lift2(b, f) }.composite(gScope)

	class Scope<F, G>(
		private val f: Applicative.Scope<F>,
		private val g: Applicative.Scope<G>,
	): Applicative.Scope<ComposeContext<F, G>> {
		override fun <A> just(a: A): CompositeApplicative<F, G, A> = CompositeApplicative(f.just(g.just(a)), g)
	}
}

fun <F, G, A> Applicative<F, Applicative<G, A>>.composite(gScope: Applicative.Scope<G>) =
	CompositeApplicative(this, gScope)

val <F, G, A> Applicative<ComposeContext<F, G>, A>.asCompose
	get() = this as CompositeApplicative<F, G, A>
