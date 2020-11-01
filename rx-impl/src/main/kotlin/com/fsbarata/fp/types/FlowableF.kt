package com.fsbarata.fp.types

import com.fsbarata.fp.concepts.*
import com.fsbarata.fp.data.Monoid
import com.fsbarata.fp.data.Semigroup
import io.reactivex.rxjava3.core.Flowable
import org.reactivestreams.Publisher
import org.reactivestreams.Subscriber

class FlowableF<A>(
	private val wrapped: Flowable<A>,
): Flowable<A>(),
   Monad<FlowableF<*>, A>,
   MonadZip<FlowableF<*>, A>,
   Publisher<A> {
	override val scope get() = Companion

	override fun subscribeActual(observer: Subscriber<in A>) {
		wrapped.subscribe(observer)
	}

	override fun <B> map(f: (A) -> B) =
		wrapped.map(f).f()

	override fun <B> bind(f: (A) -> Context<FlowableF<*>, B>): FlowableF<B> =
		flatMap { f(it).asFlowable }

	fun <B> flatMap(f: (A) -> Flowable<B>): FlowableF<B> =
		wrapped.flatMap(f).f()

	fun reduce(semigroup: Semigroup<A>) = super.reduce(semigroup::combine).f()
	fun fold(monoid: Monoid<A>) = super.reduce(monoid.empty, monoid::combine).f()
	fun scan(semigroup: Semigroup<A>) = super.scan(semigroup::combine).f()
	fun scan(monoid: Monoid<A>) = super.scan(monoid.empty, monoid::combine).f()

	override fun <B, R> zipWith(other: MonadZip<FlowableF<*>, B>, f: (A, B) -> R) =
		(this as Flowable<A>).zipWith(other.asFlowable, f).f()

	companion object: Monad.Scope<FlowableF<*>> {
		fun <A> empty() = Flowable.empty<A>().f()
		override fun <A> just(a: A) = Flowable.just(a).f()
	}
}

fun <A> Flowable<A>.f() = FlowableF(this)

val <A> Context<FlowableF<*>, A>.asFlowable
	get() = this as FlowableF<A>
