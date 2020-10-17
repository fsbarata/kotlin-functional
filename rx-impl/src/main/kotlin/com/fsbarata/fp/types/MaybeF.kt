package com.fsbarata.fp.types

import com.fsbarata.fp.concepts.Context
import com.fsbarata.fp.concepts.Functor
import com.fsbarata.fp.concepts.Monad
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.core.MaybeObserver
import io.reactivex.rxjava3.core.MaybeSource

class MaybeF<A>(
	private val wrapped: Maybe<A>,
): Maybe<A>(),
   Monad<MaybeF<*>, A>,
   MaybeSource<A> {
	override val scope get() = Companion

	override fun subscribeActual(observer: MaybeObserver<in A>) {
		wrapped.subscribe(observer)
	}

	override fun <B> map(f: (A) -> B) =
		wrapped.map(f).f()

	override fun <B> bind(f: (A) -> Functor<MaybeF<*>, B>): MaybeF<B> =
		flatMap { f(it).asMaybe }

	fun <B> flatMap(f: (A) -> Maybe<B>): MaybeF<B> =
		wrapped.flatMap(f).f()

	companion object: Monad.Scope<MaybeF<*>> {
		fun <A> empty() = Maybe.empty<A>().f()
		override fun <A> just(a: A) = Maybe.just(a).f()
	}
}

fun <A> Maybe<A>.f() = MaybeF(this)

val <A> Context<MaybeF<*>, A>.asMaybe
	get() = this as MaybeF<A>
