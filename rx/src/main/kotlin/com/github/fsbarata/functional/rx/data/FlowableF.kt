package com.github.fsbarata.functional.rx.data

import com.github.fsbarata.functional.Context
import com.github.fsbarata.functional.control.*
import com.github.fsbarata.functional.data.Monoid
import com.github.fsbarata.functional.data.Semigroup
import io.reactivex.rxjava3.core.Flowable
import org.reactivestreams.Publisher
import org.reactivestreams.Subscriber

class FlowableF<A>(private val wrapped: Flowable<A>): Flowable<A>(),
	MonadZip<FlowableF<*>, A> {
	override val scope get() = Companion

	override fun subscribeActual(observer: Subscriber<in A>) {
		wrapped.subscribe(observer)
	}

	override fun <B> map(f: (A) -> B) =
		wrapped.map(f).f()

	override infix fun <B> bind(f: (A) -> Context<FlowableF<*>, B>): FlowableF<B> =
		wrapped.switchMap { f(it).asFlowable }.f()

	fun <B> flatMap(f: (A) -> Publisher<B>): FlowableF<B> =
		wrapped.flatMap(f).f()

	fun fold(monoid: Monoid<A>) = super.reduce(monoid.empty, monoid::combine).f()
	fun scan(monoid: Monoid<A>) = super.scan(monoid.empty, monoid::combine).f()

	override fun <B, R> zipWith(other: MonadZip<FlowableF<*>, B>, f: (A, B) -> R) =
		(this as Flowable<A>).zipWith(other.asFlowable, f).f()

	companion object: Monad.Scope<FlowableF<*>> {
		fun <A> empty() = Flowable.empty<A>().f()
		override fun <A> just(a: A) = Flowable.just(a).f()
	}
}

fun <A: Semigroup<A>> Flowable<A>.reduce() = reduce { a1, a2 -> a1.combineWith(a2) }.f()
fun <A: Semigroup<A>> Flowable<A>.fold(initialValue: A) = reduce(initialValue) { a1, a2 -> a1.combineWith(a2) }.f()
fun <A: Semigroup<A>> Flowable<A>.scan() = scan { a1, a2 -> a1.combineWith(a2) }.f()
fun <A: Semigroup<A>> Flowable<A>.scan(initialValue: A) = scan(initialValue) { a1, a2 -> a1.combineWith(a2) }.f()

fun <A> Flowable<A>.f() = FlowableF(this)
fun <A, R> Flowable<A>.f(block: FlowableF<A>.() -> Context<FlowableF<*>, R>) =
	f().block().asFlowable

val <A> Context<FlowableF<*>, A>.asFlowable
	get() = this as FlowableF<A>

operator fun <A, B, R> Lift2<A, B, R>.invoke(
	flow1: Flowable<A>,
	flow2: Flowable<B>,
): FlowableF<R> = Flowable.combineLatest(flow1, flow2, f).f()

operator fun <A, B, C, R> Lift3<A, B, C, R>.invoke(
	flow1: Flowable<A>,
	flow2: Flowable<B>,
	flow3: Flowable<C>,
): FlowableF<R> = Flowable.combineLatest(flow1, flow2, flow3, f).f()

operator fun <A, B, C, D, R> Lift4<A, B, C, D, R>.invoke(
	flow1: Flowable<A>,
	flow2: Flowable<B>,
	flow3: Flowable<C>,
	flow4: Flowable<D>,
): FlowableF<R> = Flowable.combineLatest(flow1, flow2, flow3, flow4, f).f()
