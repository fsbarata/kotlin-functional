package com.github.fsbarata.functional.data

interface Contravariant<F, A> {
	fun <B> contramap(f: (B) -> A): Contravariant<F, B>
}

