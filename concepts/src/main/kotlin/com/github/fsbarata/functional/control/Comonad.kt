package com.github.fsbarata.functional.control

import com.github.fsbarata.functional.data.id

interface Comonad<W, out A>: Functor<W, A> {
	fun extract(): A

	fun duplicate(): Comonad<W, Comonad<W, A>> =
		extend(id())

	fun <B> extend(f: (Comonad<W, A>) -> B): Comonad<W, B> =
		duplicate().map(f)

	override fun <B> map(f: (A) -> B): Comonad<W, B> =
		extend { f(it.extract()) }
}
