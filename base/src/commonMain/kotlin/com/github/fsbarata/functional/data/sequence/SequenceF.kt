package com.github.fsbarata.functional.data.sequence

import com.github.fsbarata.functional.Context
import com.github.fsbarata.functional.control.*
import com.github.fsbarata.functional.data.*
import com.github.fsbarata.functional.data.list.ListF
import com.github.fsbarata.functional.data.list.NonEmptyList
import com.github.fsbarata.functional.data.maybe.Optional
import com.github.fsbarata.functional.data.set.SetF
import com.github.fsbarata.functional.utils.singleItemIterator
import com.github.fsbarata.io.Serializable

@Suppress("OVERRIDE_BY_INLINE")
class SequenceF<out A>(private val wrapped: Sequence<A>):
	MonadZip<SequenceContext, A>,
	MonadPlus<SequenceContext, A>,
	Traversable<SequenceContext, A>,
	Semigroup<SequenceF<@UnsafeVariance A>>,
	Sequence<A> by wrapped,
	Serializable {
	override val scope get() = SequenceF

	override fun <B> map(f: (A) -> B): SequenceF<B> =
		wrapped.map(f).f()

	override fun onEach(f: (A) -> Unit): SequenceF<A> = wrapped.onEach(f).f()

	override infix fun <B> ap(ff: Context<SequenceContext, (A) -> B>): SequenceF<B> =
		wrapped.ap(ff.asSequence).f()

	override fun <B, R> lift2(fb: Context<SequenceContext, B>, f: (A, B) -> R): SequenceF<R> =
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

	override fun <B, R> zipWith(other: Context<SequenceContext, B>, f: (A, B) -> R): SequenceF<R> =
		zip(other.asSequence, f).f()

	fun <R> mapIndexed(f: (Int, A) -> R): SequenceF<R> =
		SequenceF(generateSequence(0, Int::inc).zip(wrapped, f))

	fun onEachIndexed(f: (Int, A) -> Unit): SequenceF<A> =
		mapIndexed { index, a -> f(index, a); a }

	override inline fun <F, B> traverse(
		appScope: Applicative.Scope<F>,
		f: (A) -> Context<F, B>,
	): Context<F, SequenceF<B>> =
		appScope.map((this as Sequence<A>).traverse(appScope, f), Sequence<B>::f)

	override fun combineWith(other: Context<SequenceContext, @UnsafeVariance A>) =
		SequenceF(wrapped + (other.asSequence).wrapped)

	override fun concatWith(other: SequenceF<@UnsafeVariance A>): SequenceF<A> = combineWith(other)

	override fun toString() = wrapped.toString()
	override fun equals(other: Any?) = wrapped == other
	override fun hashCode() = wrapped.hashCode()

	override fun toList(): ListF<A> = ListF.fromSequence(wrapped)
	override fun toSetF(): SetF<A> = SetF.fromSequence(wrapped)

	override fun some(): SequenceF<NonEmptyList<A>> {
		return super.some().asSequence
	}

	override fun many(): SequenceF<List<A>> {
		return super.many().asSequence
	}

	companion object:
		MonadPlus.Scope<SequenceContext>,
		MonadZip.Scope<SequenceContext>,
		Traversable.Scope<SequenceContext> {
		override fun <A> empty(): SequenceF<A> = emptySequence<A>().f()
		override fun <A> just(a: A) = Sequence { singleItemIterator(a) }.f()
		fun <A> of(vararg items: A) = sequenceOf(*items).f()

		fun <A> monoid() = monoid(empty<A>())

		override fun <A> fromIterable(iterable: Iterable<A>) = iterable.asSequence().f()
		override fun <A> fromSequence(sequence: Sequence<A>) = sequence.f()
		override fun <A> fromList(list: List<A>) = list.asSequence().f()
		override fun <A> fromOptional(optional: Optional<A>) = optional.maybe(empty(), ::just)
	}
}

fun <A> Sequence<A>.f() = when (this) {
	is SequenceF -> this
	else -> SequenceF(this)
}

internal typealias SequenceContext = SequenceF<*>

val <A> Context<SequenceContext, A>.asSequence: SequenceF<A>
	get() = this as SequenceF<A>


operator fun <A, B, R> Lift2<A, B, R>.invoke(
	sequence1: Sequence<A>,
	sequence2: Sequence<B>,
): SequenceF<R> = app(sequence1.f(), sequence2.f()).asSequence

operator fun <A, B, C, R> Lift3<A, B, C, R>.invoke(
	sequence1: Sequence<A>,
	sequence2: Sequence<B>,
	sequence3: Sequence<C>,
): SequenceF<R> = app(sequence1.f(), sequence2.f(), sequence3.f()).asSequence

operator fun <A, B, C, D, R> Lift4<A, B, C, D, R>.invoke(
	sequence1: Sequence<A>,
	sequence2: Sequence<B>,
	sequence3: Sequence<C>,
	sequence4: Sequence<D>,
): SequenceF<R> = app(sequence1.f(), sequence2.f(), sequence3.f(), sequence4.f()).asSequence
