package com.github.fsbarata.functional.data.compose

import com.github.fsbarata.functional.data.Functor

internal typealias ComposeContext<F, G> = Composed<F, G, *>

open class Composed<F, G, A>(
	open val underlying: Functor<F, Functor<G, A>>,
): Functor<ComposeContext<F, G>, A> {
	@Suppress("UNCHECKED_CAST")
	inline fun <reified T: Functor<G, A>> decompose() =
		underlying as Functor<F, T>

	override fun <B> map(f: (A) -> B): Composed<F, G, B> =
		underlying.map { g -> g.map(f) }.compose()

	override fun equals(other: Any?): Boolean {
		if (this === other) return true
		if (other !is Composed<*, *, *>) return false
		return (underlying == other.underlying)
	}

	override fun hashCode() = underlying.hashCode()

	override fun toString() = "Compose($underlying)"
}

fun <F, G, A> Functor<F, Functor<G, A>>.compose() = Composed(this)

val <F, G, A> Functor<ComposeContext<F, G>, A>.asCompose
	get() = this as Composed<F, G, A>
