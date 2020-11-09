package com.github.fsbarata.functional.data.compose

import com.github.fsbarata.functional.control.Applicative

class ComposeApplicative<F, G, A>(
	override val fg: Applicative<F, Applicative<G, A>>,
	private val gScope: Applicative.Scope<G>,
): Applicative<ComposeContext<F, G>, A>,
	Compose<F, G, A>(fg) {
	override val scope = Scope(fg.scope, gScope)

	override fun <B> map(f: (A) -> B): ComposeApplicative<F, G, B> =
		fg.map { g -> g.map(f) }.compose(gScope)

	override fun <B> ap(ff: Applicative<ComposeContext<F, G>, (A) -> B>): ComposeApplicative<F, G, B> {
		val f: Applicative<G, A>.(Applicative<G, (A) -> B>) -> Applicative<G, B> = Applicative<G, A>::ap
		return fg.lift2(ff.asCompose.fg, f).compose(gScope)
	}

	override fun <B, R> lift2(fb: Applicative<ComposeContext<F, G>, B>, f: (A, B) -> R): ComposeApplicative<F, G, R> =
		fg.lift2(fb.asCompose.fg) { a, b -> a.lift2(b, f) }.compose(gScope)

	class Scope<F, G>(
		private val f: Applicative.Scope<F>,
		private val g: Applicative.Scope<G>,
	): Applicative.Scope<ComposeContext<F, G>> {
		override fun <A> just(a: A): ComposeApplicative<F, G, A> = ComposeApplicative(f.just(g.just(a)), g)
	}
}

fun <F, G, A> Applicative<F, Applicative<G, A>>.compose(gScope: Applicative.Scope<G>) =
	ComposeApplicative(this, gScope)

val <F, G, A> Applicative<ComposeContext<F, G>, A>.asCompose
	get() = this as ComposeApplicative<F, G, A>
