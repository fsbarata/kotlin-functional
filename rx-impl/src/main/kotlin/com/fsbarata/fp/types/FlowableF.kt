package com.fsbarata.fp.types

import com.fsbarata.fp.concepts.*
import io.reactivex.rxjava3.core.Flowable
import org.reactivestreams.Subscriber

class FlowableF<A>(
		private val wrapped: Flowable<A>
) : Flowable<A>(),
		Monad<Flowable<*>, A> {
	override fun subscribeActual(observer: Subscriber<in A>) {
		wrapped.subscribe(observer)
	}

	override fun <B> just(b: B): FlowableF<B> =
			Companion.just(b)

	override fun <B> map(f: (A) -> B) =
			wrapped.map(f).f()

	override fun <B> flatMap(f: (A) -> Functor<Flowable<*>, B>): FlowableF<B> =
			wrapped.flatMap { f(it).asFlowable }.f()

	fun reduce(semigroup: Semigroup<A>) = with(semigroup) { reduce { a1, a2 -> a1.combine(a2) } }.f()
	fun fold(initialValue: A, semigroup: Semigroup<A>) = with(semigroup) { reduce(initialValue) { a1, a2 -> a1.combine(a2) } }.f()
	fun fold(monoid: Monoid<A>) = with(monoid) { reduce(empty()) { a1, a2 -> a1.combine(a2) } }.f()
	fun scan(semigroup: Semigroup<A>) = with(semigroup) { scan { a1, a2 -> a1.combine(a2) } }.f()
	fun scan(initialValue: A, semigroup: Semigroup<A>) = with(semigroup) { scan(initialValue) { a1, a2 -> a1.combine(a2) } }.f()
	fun scan(monoid: Monoid<A>) = with(monoid) { scan(empty()) { a1, a2 -> a1.combine(a2) } }.f()

	companion object {
		fun <A> empty() = Flowable.empty<A>().f()
		fun <A> just(a: A) = Flowable.just(a).f()
	}
}

fun <A> Flowable<A>.f() = FlowableF(this)

val <A> Context<Flowable<*>, A>.asFlowable
	get() = this as FlowableF<A>
