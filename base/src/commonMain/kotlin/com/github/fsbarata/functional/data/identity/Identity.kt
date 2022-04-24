package com.github.fsbarata.functional.data.identity

import com.github.fsbarata.functional.Context
import com.github.fsbarata.functional.control.Comonad
import com.github.fsbarata.functional.control.Monad
import com.github.fsbarata.functional.control.MonadZip
import com.github.fsbarata.functional.data.Foldable
import com.github.fsbarata.functional.data.Monoid
import com.github.fsbarata.functional.data.partial
import com.github.fsbarata.io.Serializable

@Suppress("OVERRIDE_BY_INLINE")
data class Identity<A>(val a: A):
	MonadZip<IdentityContext, A>,
	Comonad<IdentityContext, A>,
	Foldable<A>,
	Serializable {
	override val scope = Identity

	override fun extract(): A = a

	override fun duplicate() = Identity(this)

	override inline fun <B> extend(f: (Comonad<IdentityContext, A>) -> B) = coflatMap(f)

	inline fun <B> coflatMap(f: (Identity<A>) -> B): Identity<B> = Identity(f(this))

	override inline fun <R> foldL(initialValue: R, accumulator: (R, A) -> R) =
		accumulator(initialValue, a)

	override inline fun <R> foldR(initialValue: R, accumulator: (A, R) -> R) =
		accumulator(a, initialValue)

	override inline fun <M> foldMap(monoid: Monoid<M>, f: (A) -> M) = f(a)

	override inline fun <B> map(f: (A) -> B) = Identity(f(a))

	override infix fun <B> ap(ff: Context<IdentityContext, (A) -> B>): Identity<B> =
		scope.map(ff) { it(a) }.asIdentity

	override inline fun <B, R> lift2(fb: Context<IdentityContext, B>, f: (A, B) -> R): Identity<R> =
		Identity(f(a, fb.asIdentity.a))

	override inline infix fun <B> bind(f: (A) -> Context<IdentityContext, B>) = f(a).asIdentity

	inline fun <B> flatMap(f: (A) -> Identity<B>) = f(a)

	override inline fun <B, R> zipWith(other: Context<IdentityContext, B>, f: (A, B) -> R): Identity<R> =
		other.asIdentity.map(f.partial(a))

	companion object: Monad.Scope<IdentityContext> {
		override fun <A> just(a: A) = Identity(a)
	}
}

internal typealias IdentityContext = Identity<*>

val <A> Context<IdentityContext, A>.asIdentity
	get() = this as Identity<A>

fun <A> Context<IdentityContext, A>.runIdentity() = asIdentity.a
