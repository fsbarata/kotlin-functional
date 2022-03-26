package com.github.fsbarata.functional.data.flow

import com.github.fsbarata.functional.Context
import com.github.fsbarata.functional.control.*
import com.github.fsbarata.functional.data.Functor
import com.github.fsbarata.functional.data.Monoid
import com.github.fsbarata.functional.data.Semigroup
import com.github.fsbarata.functional.data.id
import com.github.fsbarata.functional.data.maybe.Optional
import kotlinx.coroutines.flow.*

class FlowF<A>(private val wrapped: Flow<A>): Flow<A>,
	MonadZip<FlowF<*>, A>,
	MonadPlus<FlowF<*>, A>,
	Semigroup<FlowF<A>> {
	override val scope get() = Companion

	override suspend fun collect(collector: FlowCollector<A>) {
		wrapped.collect(collector)
	}

	override fun <B> map(f: (A) -> B) =
		wrapped.map(f).f()

	override infix fun <B> bind(f: (A) -> Context<FlowF<*>, B>): FlowF<B> =
		wrapped.flatMapLatest { f(it).asFlow }.f()

	fun <B> flatMap(f: (A) -> Flow<B>): FlowF<B> =
		wrapped.flatMapMerge(transform = f).f()

	override fun <B, R> zipWith(other: Functor<FlowF<*>, B>, f: (A, B) -> R) =
		(this as Flow<A>).zip(other.asFlow, f).f()

	override fun combineWith(other: FlowF<A>) = merge(this, other).f()
	override fun associateWith(other: Context<FlowF<*>, A>) =
		combineWith(other.asFlow)

	companion object: MonadPlus.Scope<FlowF<*>> {
		override fun <A> empty() = emptyFlow<A>().f()
		override fun <A> just(a: A) = flowOf(a).f()

		override fun <A> fromList(list: List<A>) = list.asFlow().f()
		override fun <A> fromOptional(optional: Optional<A>) = optional.maybe(empty(), ::just)
	}
}

suspend fun <A> Flow<A>.fold(monoid: Monoid<A>): A = fold(monoid.empty, monoid::combine)
fun <A> Flow<A>.scan(monoid: Monoid<A>): FlowF<A> = scan(monoid.empty, monoid::combine).f()


suspend fun <A: Semigroup<A>> Flow<A>.reduce(): A = reduce { a1, a2 -> a1.combineWith(a2) }
suspend fun <A: Semigroup<A>> Flow<A>.fold(initialValue: A): A = fold(initialValue) { a1, a2 -> a1.combineWith(a2) }
fun <A: Semigroup<A>> Flow<A>.scan(): FlowF<A> = runningReduce { a1, a2 -> a1.combineWith(a2) }.f()
fun <A: Semigroup<A>> Flow<A>.scan(initialValue: A): FlowF<A> = scan(initialValue) { a1, a2 -> a1.combineWith(a2) }.f()

fun <A: Any, R: Any> Flow<A>.mapNotNone(f: (A) -> Optional<R>): Flow<R> =
	mapNotNull { f(it).orNull() }

fun <A: Any> Flow<Optional<A>>.filterNotNone(): Flow<A> =
	mapNotNone(id())

fun <A: Any> Flow<A>.partition(predicate: (A) -> Boolean): Pair<Flow<A>, Flow<A>> =
	Pair(filter(predicate), filter { !predicate(it) })

fun <A> Flow<A>.f() = FlowF(this)
fun <A: Any, R: Any> Flow<A>.f(block: FlowF<A>.() -> Context<FlowF<*>, R>) =
	f().block().asFlow

val <A> Context<FlowF<*>, A>.asFlow
	get() = this as FlowF<A>

operator fun <A: Any, B: Any, R: Any> Lift2<A, B, R>.invoke(
	flow1: Flow<A>,
	flow2: Flow<B>,
): FlowF<R> = combine(flow1, flow2, f).f()

operator fun <A: Any, B: Any, C: Any, R: Any> Lift3<A, B, C, R>.invoke(
	flow1: Flow<A>,
	flow2: Flow<B>,
	flow3: Flow<C>,
): FlowF<R> = combine(flow1, flow2, flow3, f).f()

operator fun <A: Any, B: Any, C: Any, D: Any, R: Any> Lift4<A, B, C, D, R>.invoke(
	flow1: Flow<A>,
	flow2: Flow<B>,
	flow3: Flow<C>,
	flow4: Flow<D>,
): FlowF<R> = combine(flow1, flow2, flow3, flow4, f).f()
