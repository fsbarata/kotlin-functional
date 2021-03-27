package com.github.fsbarata.functional.data.sequence

import com.github.fsbarata.functional.Context
import com.github.fsbarata.functional.control.*
import com.github.fsbarata.functional.data.Monoid
import com.github.fsbarata.functional.data.Semigroup
import com.github.fsbarata.functional.data.Traversable
import com.github.fsbarata.functional.data.maybe.Optional
import com.github.fsbarata.functional.data.monoid
import java.io.Serializable

@Suppress("OVERRIDE_BY_INLINE")
class SequenceF<A>(private val wrapped: Sequence<A>):
	MonadZip<SequenceContext, A>,
	MonadPlus<SequenceContext, A>,
	Traversable<SequenceContext, A>,
	Semigroup<SequenceF<A>>,
	Sequence<A> by wrapped,
	Serializable {
	override val scope get() = SequenceF

	override fun <B> map(f: (A) -> B) =
		wrapped.map(f).f()

	override infix fun <B> ap(ff: Applicative<SequenceContext, (A) -> B>): SequenceF<B> =
		wrapped.ap(ff.asSequence).f()

	override fun <B, R> lift2(fb: Applicative<SequenceContext, B>, f: (A, B) -> R): SequenceF<R> =
		wrapped.lift2(fb.asSequence, f).f()

	override infix fun <B> bind(f: (A) -> Context<SequenceContext, B>) =
		flatMap { f(it).asSequence }

	fun <B> flatMap(f: (A) -> Sequence<B>) =
		wrapped.flatMap(f).f()

	override fun filter(predicate: (A) -> Boolean) =
		wrapped.filter(predicate).f()

	override inline fun partition(predicate: (A) -> Boolean): Pair<SequenceF<A>, SequenceF<A>> {
		val p = (this as Sequence<A>).partition(predicate)
		return Pair(p.first.asSequence().f(), p.second.asSequence().f())
	}

	override fun <B: Any> mapNotNull(f: (A) -> B?) =
		(this as Sequence<A>).mapNotNull(f).f()

	override fun <B: Any> mapNotNone(f: (A) -> Optional<B>) =
		(this as Sequence<A>).mapNotNull { f(it).orNull() }.f()

	override inline fun <R> foldL(initialValue: R, accumulator: (R, A) -> R): R =
		(this as Sequence<A>).fold(initialValue, accumulator)

	override fun <M> foldMap(monoid: Monoid<M>, f: (A) -> M) =
		(this as Sequence<A>).foldMap(monoid, f)

	override fun <B, R> zipWith(other: MonadZip<SequenceContext, B>, f: (A, B) -> R): SequenceF<R> =
		zip(other.asSequence, f).f()

	override inline fun <F, B> traverse(
		appScope: Applicative.Scope<F>,
		f: (A) -> Applicative<F, B>,
	): Applicative<F, SequenceF<B>> =
		(this as Sequence<A>).traverse(appScope, f).map(Sequence<B>::f)

	override fun associateWith(other: Context<SequenceContext, A>) =
		SequenceF(wrapped + (other.asSequence).wrapped)

	override fun combineWith(other: SequenceF<A>): SequenceF<A> = associateWith(other)

	override fun toString() = wrapped.toString()
	override fun equals(other: Any?) = wrapped == other
	override fun hashCode() = wrapped.hashCode()

	companion object:
		MonadPlus.Scope<SequenceContext>,
		Traversable.Scope<SequenceContext> {
		override fun <A> empty(): SequenceF<A> = emptySequence<A>().f()
		override fun <A> just(a: A) = sequenceOf(a).f()
		fun <A> of(vararg items: A) = sequenceOf(*items).f()

		fun <A> monoid() = monoid(empty<A>())

		override fun <A> fromList(list: List<A>) = list.asSequence().f()
		override fun <A> fromOptional(optional: Optional<A>) = optional.maybe(empty(), ::just)
	}
}

fun <A> Sequence<A>.f() = SequenceF(this)
fun <A, R> Sequence<A>.f(block: SequenceF<A>.() -> Context<SequenceContext, R>) =
	f().block().asSequence

internal typealias SequenceContext = SequenceF<*>

val <A> Context<SequenceContext, A>.asSequence: SequenceF<A>
	get() = this as SequenceF<A>


operator fun <A, B, R> Lift2<A, B, R>.invoke(
	sequence1: SequenceF<A>,
	sequence2: SequenceF<B>,
): SequenceF<R> = app(sequence1, sequence2).asSequence

operator fun <A, B, C, R> Lift3<A, B, C, R>.invoke(
	sequence1: SequenceF<A>,
	sequence2: SequenceF<B>,
	sequence3: SequenceF<C>,
): SequenceF<R> = app(sequence1, sequence2, sequence3).asSequence

operator fun <A, B, C, D, R> Lift4<A, B, C, D, R>.invoke(
	sequence1: SequenceF<A>,
	sequence2: SequenceF<B>,
	sequence3: SequenceF<C>,
	sequence4: SequenceF<D>,
): SequenceF<R> = app(sequence1, sequence2, sequence3, sequence4).asSequence
