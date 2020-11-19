package com.github.fsbarata.functional.rx.data

import com.github.fsbarata.functional.Context
import com.github.fsbarata.functional.control.Monad
import com.github.fsbarata.functional.control.MonadZip
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.core.MaybeObserver

class MaybeF<A>(private val wrapped: Maybe<A>): Maybe<A>(),
	MonadZip<MaybeF<*>, A> {
	override val scope get() = Companion

	override fun subscribeActual(observer: MaybeObserver<in A>) {
		wrapped.subscribe(observer)
	}

	override fun <B> map(f: (A) -> B) =
		wrapped.map(f).f()

	override infix fun <B> bind(f: (A) -> Context<MaybeF<*>, B>): MaybeF<B> =
		flatMap { f(it).asMaybe }

	fun <B> flatMap(f: (A) -> Maybe<B>): MaybeF<B> =
		wrapped.flatMap(f).f()

	override fun <B, R> zipWith(other: MonadZip<MaybeF<*>, B>, f: (A, B) -> R) =
		(this as Maybe<A>).zipWith(other.asMaybe, f).f()

	companion object: Monad.Scope<MaybeF<*>> {
		fun <A> empty() = Maybe.empty<A>().f()
		override fun <A> just(a: A) = Maybe.just(a).f()
	}
}

fun <A> Maybe<A>.f() = MaybeF(this)

val <A> Context<MaybeF<*>, A>.asMaybe
	get() = this as MaybeF<A>
