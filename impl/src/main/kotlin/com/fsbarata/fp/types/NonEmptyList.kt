package com.fsbarata.fp.types

import com.fsbarata.fp.concepts.Context
import com.fsbarata.fp.concepts.Foldable
import com.fsbarata.fp.concepts.Functor
import com.fsbarata.fp.concepts.Monad

class NonEmptyList<A> private constructor(
		private val list: List<A>
) : Monad<NonEmptyList<*>, A>,
		Foldable<A>,
		List<A> by list {
	override fun <B> just(b: B): NonEmptyList<B> =
			Companion.just(b)

	override fun <B> map(f: (A) -> B): NonEmptyList<B> =
			NonEmptyList(list.map(f))

	override fun <B> flatMap(f: (A) -> Functor<NonEmptyList<*>, B>): NonEmptyList<B> =
			NonEmptyList(list.flatMap { f(it).asNel })

	companion object {
		fun <T> just(item: T) = NonEmptyList(listOf(item))
	}

	override fun <R> fold(initialValue: R, accumulator: (R, A) -> R): R =
			list.fold(initialValue, accumulator)
}

val <A> Context<NonEmptyList<*>, A>.asNel get() = this as NonEmptyList<A>