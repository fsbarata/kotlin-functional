package com.fsbarata.fp.types

import com.fsbarata.fp.concepts.*
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.core.MaybeObserver
import io.reactivex.rxjava3.core.MaybeSource

class MaybeF<A>(
		private val wrapped: Maybe<A>
) : Maybe<A>(),
		Monad<Maybe<*>, A>,
		MaybeSource<A> {
	override fun subscribeActual(observer: MaybeObserver<in A>) {
		wrapped.subscribe(observer)
	}

	override fun <B> just(b: B): MaybeF<B> =
			Companion.just(b)

	override fun <B> map(f: (A) -> B) =
			wrapped.map(f).f()

	override fun <B> flatMap(f: (A) -> Functor<Maybe<*>, B>): MaybeF<B> =
			wrapped.flatMap { f(it).asMaybe }.f()

	companion object {
		fun <A> empty() = Maybe.empty<A>().f()
		fun <A> just(a: A) = Maybe.just(a).f()
	}
}

fun <A> Maybe<A>.f() = MaybeF(this)

val <A> Context<Maybe<*>, A>.asMaybe
	get() = this as MaybeF<A>
