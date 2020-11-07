package com.github.fsbarata.functional.data.identity

import com.github.fsbarata.functional.control.Applicative
import com.github.fsbarata.functional.control.Context
import com.github.fsbarata.functional.control.Monad
import com.github.fsbarata.functional.control.MonadZip
import com.github.fsbarata.functional.data.Foldable
import com.github.fsbarata.functional.data.Monoid
import com.github.fsbarata.functional.data.partial

data class Identity<A>(
	val a: A,
): Monad<IdentityContext, A>,
	MonadZip<IdentityContext, A>,
	Foldable<A> {
	override val scope = Identity

	override fun <R> foldL(initialValue: R, accumulator: (R, A) -> R) =
		accumulator(initialValue, a)

	override fun <R> foldR(initialValue: R, accumulator: (A, R) -> R) =
		accumulator(a, initialValue)

	override fun <M> foldMap(monoid: Monoid<M>, f: (A) -> M) = f(a)

	@Suppress("OVERRIDE_BY_INLINE")
	override inline fun <B> map(f: (A) -> B) = Identity(f(a))

	override fun <B> ap(ff: Applicative<IdentityContext, (A) -> B>): Identity<B> =
		ff.map { it(a) }.asIdentity

	@Suppress("OVERRIDE_BY_INLINE")
	override inline fun <B, D> liftA2(f: (A) -> (B) -> D): (Applicative<IdentityContext, B>) -> Identity<D> {
		val f2 = f(a)
		return { ib -> ib.asIdentity.map(f2) }
	}

	@Suppress("OVERRIDE_BY_INLINE")
	override inline fun <B> bind(f: (A) -> Context<IdentityContext, B>) = f(a).asIdentity

	inline fun <B> flatMap(f: (A) -> Identity<B>) = f(a)

	override fun <B, R> zipWith(other: MonadZip<IdentityContext, B>, f: (A, B) -> R): Identity<R> =
		other.asIdentity.map(f.partial(a))

	companion object: Monad.Scope<IdentityContext> {
		override fun <A> just(a: A) = Identity(a)
	}
}

private typealias IdentityContext = Identity<*>

val <A> Context<IdentityContext, A>.asIdentity
	get() = this as Identity<A>
