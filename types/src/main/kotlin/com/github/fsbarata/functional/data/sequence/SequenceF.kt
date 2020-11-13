package com.github.fsbarata.functional.data.sequence

import com.github.fsbarata.functional.control.*
import com.github.fsbarata.functional.data.Monoid
import com.github.fsbarata.functional.data.Traversable
import com.github.fsbarata.functional.data.monoid
import java.io.Serializable

@Suppress("OVERRIDE_BY_INLINE")
class SequenceF<A>(
	private val wrapped: Sequence<A>,
): Monad<SequenceContext, A>,
	MonadZip<SequenceContext, A>,
	Traversable<SequenceContext, A>,
	Alternative<SequenceContext, A>,
	Sequence<A> by wrapped,
	Serializable {
	override val scope get() = SequenceF

	override fun <B> map(f: (A) -> B) =
		(this as Sequence<A>).map(f).f()

	override infix fun <B> ap(ff: Applicative<SequenceContext, (A) -> B>): SequenceF<B> =
		wrapped.ap(ff.asSequence).f()

	override fun <B, R> lift2(fb: Applicative<SequenceContext, B>, f: (A, B) -> R): SequenceF<R> =
		(this as Sequence<A>).lift2(fb.asSequence, f).f()

	override infix fun <B> bind(f: (A) -> Context<SequenceContext, B>) =
		flatMap { f(it).asSequence }

	fun <B> flatMap(f: (A) -> Sequence<B>) =
		(this as Sequence<A>).flatMap(f).f()

	override inline fun <R> foldL(initialValue: R, accumulator: (R, A) -> R): R =
		(this as Sequence<A>).fold(initialValue, accumulator)

	override fun <M> foldMap(monoid: Monoid<M>, f: (A) -> M) =
		(this as Sequence<A>).foldMap(monoid, f)

	override fun <B, R> zipWith(other: MonadZip<SequenceContext, B>, f: (A, B) -> R): SequenceF<R> =
		zip(other.asSequence, f).f()

	override fun <F, B> traverse(
		appScope: Applicative.Scope<F>,
		f: (A) -> Applicative<F, B>,
	): Applicative<F, SequenceF<B>> =
		(this as Sequence<A>).traverse(appScope, f).map(Sequence<B>::f)

	override fun associateWith(other: Alternative<SequenceContext, A>) =
		SequenceF(wrapped + (other.asSequence).wrapped)

	override fun toString() = wrapped.toString()
	override fun equals(other: Any?) = wrapped == other
	override fun hashCode() = wrapped.hashCode()

	companion object:
		Monad.Scope<SequenceContext>,
		Traversable.Scope<SequenceContext>,
		Alternative.Scope<SequenceContext> {
		override fun <A> empty() = emptySequence<A>().f()
		override fun <A> just(a: A) = sequenceOf(a).f()
		fun <A> of(vararg items: A) = sequenceOf(*items).f()

		fun <A> concatMonoid() = monoid(empty(), Sequence<A>::plus)
	}
}

fun <A> Sequence<A>.f() = SequenceF(this)

internal typealias SequenceContext = SequenceF<*>

val <A> Context<SequenceContext, A>.asSequence: SequenceF<A>
	get() = this as SequenceF<A>

