package com.github.fsbarata.functional.rx.data

import com.github.fsbarata.functional.Context
import com.github.fsbarata.functional.control.Monad
import com.github.fsbarata.functional.control.MonadZip
import com.github.fsbarata.functional.data.Monoid
import com.github.fsbarata.functional.data.Semigroup
import io.reactivex.rxjava3.core.Flowable
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
		flatMap { f(it).asFlowable }

	fun <B> flatMap(f: (A) -> Flowable<B>): FlowableF<B> =
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

val <A> Context<FlowableF<*>, A>.asFlowable
	get() = this as FlowableF<A>
