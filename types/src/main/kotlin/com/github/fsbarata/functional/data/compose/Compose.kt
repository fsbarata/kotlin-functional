package com.github.fsbarata.functional.data.compose

import com.github.fsbarata.functional.control.Functor

internal typealias ComposeContext<F, G> = Compose<F, G, *>

open class Compose<F, G, A>(
	open val fg: Functor<F, Functor<G, A>>,
): Functor<ComposeContext<F, G>, A> {
	override fun <B> map(f: (A) -> B): Compose<F, G, B> =
		fg.map { g -> g.map(f) }.compose()

	override fun equals(other: Any?): Boolean {
		if (this === other) return true
		if (other !is Compose<*, *, *>) return false
		return (fg == other.fg)
	}

	override fun hashCode() = fg.hashCode()

	override fun toString() = "Compose(${fg.toString()})"
}

fun <F, G, A> Functor<F, Functor<G, A>>.compose() = Compose(this)

val <F, G, A> Functor<ComposeContext<F, G>, A>.asCompose
	get() = this as Compose<F, G, A>
