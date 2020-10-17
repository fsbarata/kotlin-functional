package com.fsbarata.fp.types

import com.fsbarata.fp.concepts.*
import io.reactivex.rxjava3.core.Flowable
import org.reactivestreams.Subscriber

class FlowableF<A>(
	private val wrapped: Flowable<A>,
): Flowable<A>(),
   Monad<FlowableF<*>, A> {
	override val scope get() = Companion

	override fun subscribeActual(observer: Subscriber<in A>) {
		wrapped.subscribe(observer)
	}

	override fun <B> map(f: (A) -> B) =
		wrapped.map(f).f()

	override fun <B> bind(f: (A) -> Functor<FlowableF<*>, B>): FlowableF<B> =
		flatMap { f(it).asFlowable }

	fun <B> flatMap(f: (A) -> Flowable<B>): FlowableF<B> =
		wrapped.flatMap(f).f()

	fun reduce(semigroup: Semigroup<A>) = with(semigroup) { reduce { a1, a2 -> a1.combine(a2) } }.f()
	fun fold(monoid: Monoid<A>) = with(monoid) { reduce(empty) { a1, a2 -> a1.combine(a2) } }.f()
	fun scan(semigroup: Semigroup<A>) = with(semigroup) { scan { a1, a2 -> a1.combine(a2) } }.f()
	fun scan(monoid: Monoid<A>) = with(monoid) { scan(empty) { a1, a2 -> a1.combine(a2) } }.f()

	companion object: Monad.Scope<FlowableF<*>> {
		fun <A> empty() = Flowable.empty<A>().f()
		override fun <A> just(a: A) = Flowable.just(a).f()
	}
}

fun <A> Flowable<A>.f() = FlowableF(this)

val <A> Context<FlowableF<*>, A>.asFlowable
	get() = this as FlowableF<A>
