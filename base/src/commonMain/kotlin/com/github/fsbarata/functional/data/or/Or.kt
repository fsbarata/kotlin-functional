package com.github.fsbarata.functional.data.or

import com.github.fsbarata.functional.BiContext
import com.github.fsbarata.functional.Context
import com.github.fsbarata.functional.control.Applicative
import com.github.fsbarata.functional.control.Monad
import com.github.fsbarata.functional.data.*
import com.github.fsbarata.io.Serializable

/**
 * Or Functor
 *
 * Inclusive Or between two values. Biased right for Functor and Foldable operations and flatMap
 */
@Suppress("OVERRIDE_BY_INLINE")
sealed class Or<out L, out R>:
	BiFunctor<OrBiContext, L, R>,
	Traversable<OrContext<@UnsafeVariance L>, R>,
	Serializable {

	override val scope get() = Scope<@UnsafeVariance L>()

	data class Left<out A>(val value: A): Or<A, Nothing>()
	data class Right<out A>(val value: A): Or<Nothing, A>()
	data class Both<out L, out R>(val left: L, val right: R): Or<L, R>() {
		inline fun asPair(): Pair<L, R> = Pair(left, right)
	}

	inline fun <T> fold(ifLeft: (L) -> T, ifRight: (R) -> T, ifBoth: (L, R) -> T): T = when (this) {
		is Left -> ifLeft(value)
		is Right -> ifRight(value)
		is Both -> ifBoth(left, right)
	}

	final override inline fun <A> map(f: (R) -> A): Or<L, A> {
		return bimap(id(), f)
	}

	final override inline fun <A> mapLeft(f: (L) -> A): Or<A, R> {
		return bimap(f, id())
	}

	final override inline fun <A, B> bimap(f: (L) -> A, g: (R) -> B): Or<A, B> {
		return when (this) {
			is Left -> Left(f(value))
			is Right -> Right(g(value))
			is Both -> Both(f(left), g(right))
		}
	}

	final override inline fun <M> foldMap(monoid: Monoid<M>, f: (R) -> M): M {
		return f(rightOrNull() ?: return monoid.empty)
	}

	final override inline fun <A> foldL(initialValue: A, accumulator: (A, R) -> A): A {
		return accumulator(initialValue, rightOrNull() ?: return initialValue)
	}

	final override inline fun <A> foldR(initialValue: A, accumulator: (R, A) -> A): A {
		return accumulator(rightOrNull() ?: return initialValue, initialValue)
	}

	final override inline fun <F, B> traverse(
		appScope: Applicative.Scope<F>,
		f: (R) -> Context<F, B>,
	): Context<F, Or<L, B>> = when (this) {
		is Left -> appScope.just(this)
		is Right -> appScope.map(f(value), ::Right)
		is Both -> appScope.map(f(right)) { Both(left, it) }
	}

	inline fun swap(): Or<R, L> = when (this) {
		is Left -> Right(value)
		is Right -> Left(value)
		is Both -> Both(right, left)
	}

	inline fun toPair(): Pair<L?, R?> = Pair(leftOrNull(), rightOrNull())

	inline fun leftOrNull(): L? = when (this) {
		is Left -> value
		is Right -> null
		is Both -> left
	}

	inline fun rightOrNull(): R? = when (this) {
		is Left -> null
		is Right -> value
		is Both -> right
	}

	class Scope<L>: Traversable.Scope<OrContext<L>>

	class MonadScope<L>(private val semigroupScope: Semigroup.Scope<L>): Monad.Scope<OrContext<L>> {
		override fun <R> just(a: R) = right<L, R>(a)

		override fun <A, B> bind(
			ca: Context<OrContext<L>, A>,
			f: (A) -> Context<OrContext<L>, B>,
		): Context<OrContext<L>, B> = ca.flatMap(semigroupScope, f)
	}

	companion object: Traversable.Scope<OrContext<Nothing>> {
		fun <L, R> left(value: L): Or<L, R> = Left(value)
		fun <L, R> right(value: R): Or<L, R> = Right(value)

		fun <L> monad(semigroupScope: Semigroup.Scope<L>) = MonadScope(semigroupScope)
		fun <L: Semigroup<L>> monad() = monad(semigroupScopeOf<L>())
	}
}

typealias OrContext<A> = Or<A, *>
typealias OrBiContext = OrContext<*>

val <L, R> Context<OrContext<L>, R>.asOr
	get() = this as Or<L, R>

val <L, R> BiContext<OrBiContext, L, R>.asOr
	get() = this as Or<L, R>

inline fun <L: Semigroup<L>, R, A> Context<OrContext<L>, R>.flatMap(f: (R) -> Context<OrContext<L>, A>): Or<L, A> {
	return flatMap(semigroupScopeOf(), f)
}

inline fun <L, R, A> Context<OrContext<L>, R>.flatMap(
	semigroupScope: Semigroup.Scope<L>,
	f: (R) -> Context<OrContext<L>, A>,
): Or<L, A> {
	return when (val t = asOr) {
		is Or.Left -> t
		is Or.Right -> f(t.value).asOr
		is Or.Both -> when (val fm = f(t.right).asOr) {
			is Or.Left -> Or.Left(semigroupScope.concat(t.left, fm.value))
			is Or.Right -> Or.Both(t.left, fm.value)
			is Or.Both -> Or.Both(semigroupScope.concat(t.left, fm.left), fm.right)
		}
	}
}

inline fun <A, B> Pair<A, B>.asOr(): Or.Both<A, B> = Or.Both(first, second)
inline fun <A, B: Any> Pair<A, B?>.firstOrBoth(): Or<A, B> {
	return Or.Both(first, second ?: return Or.Left(first))
}

inline fun <A: Any, B> Pair<A?, B>.secondOrBoth(): Or<A, B> {
	return Or.Both(first ?: return Or.Right(second), second)
}
