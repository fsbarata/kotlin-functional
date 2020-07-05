package com.fsbarata.fp.types

import com.fsbarata.fp.concepts.Functor
import com.fsbarata.fp.concepts.Context
import com.fsbarata.fp.concepts.Monad
import io.reactivex.Observable
import io.reactivex.ObservableSource

class ObservableF<A>(
		private val wrapped: Observable<A>
) : Monad<Observable<*>, A>,
		ObservableSource<A> by wrapped {
	override fun <B> just(b: B): ObservableF<B> = Companion.just(b)

	override fun <B> map(f: (A) -> B) =
			wrapped.map(f).monad()

	override fun <B> flatMap(f: (A) -> Functor<Observable<*>, B>): ObservableF<B> =
			ObservableF(wrapped.flatMap { f(it).value })

	fun toObservable() = wrapped

	companion object {
		fun <A> just(a: A) = Observable.just(a).monad()
	}
}

fun <A> Observable<A>.monad() = ObservableF(this)

val <A> Context<Observable<*>, A>.value: Observable<A>
	get() = (this as ObservableF<A>).toObservable()
