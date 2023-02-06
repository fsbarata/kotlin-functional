package com.github.fsbarata.functional.control.monad

import com.github.fsbarata.functional.Context
import com.github.fsbarata.functional.control.Monad
import com.github.fsbarata.functional.control.MonadPlus
import com.github.fsbarata.functional.control.MonadZip
import com.github.fsbarata.functional.control.lift2
import com.github.fsbarata.functional.data.maybe.*


@Suppress("OVERRIDE_BY_INLINE", "NOTHING_TO_INLINE")
class OptionalT<M, out A>(
	val wrapped: Context<M, Optional<A>>,
	private val innerScope: Monad.Scope<M>,
): MonadTrans<OptionalContext, M, A>,
	MonadPlus<Context<M, OptionalContext>, A> {

	constructor(wrapped: Monad<M, Optional<A>>): this(wrapped, wrapped.scope)

	override val scope: Scope<M> get() = OptionalTScope(innerScope)

	fun unwrap() = wrapped

	inline fun <MM> mapOptionalT(f: (Context<M, Optional<A>>) -> Monad<MM, Optional<@UnsafeVariance A>>): OptionalT<MM, @UnsafeVariance A> {
		return OptionalT(f(wrapped))
	}

	inline fun <MM> mapOptionalT(
		monadScope: Monad.Scope<MM>,
		f: (Context<M, Optional<A>>) -> Context<MM, Optional<@UnsafeVariance A>>,
	): OptionalT<MM, @UnsafeVariance A> {
		return OptionalT(f(wrapped), monadScope)
	}

	override fun <B> map(f: (A) -> B): OptionalT<M, B> =
		OptionalT(innerScope.map(wrapped) { it.map(f) }, innerScope)

	override fun <B> bind(f: (A) -> Context<Context<M, OptionalContext>, B>) =
		flatMap { f(it).asOptionalT }

	fun <B> flatMap(f: (A) -> OptionalT<M, B>): OptionalT<M, B> =
		flatMapT { a: A -> f(a).unwrap() }

	fun <B> flatMapT(f: (A) -> Context<M, Optional<B>>): OptionalT<M, B> {
		return OptionalT(
			innerScope.bind(wrapped) { v ->
				v.fold(
					ifEmpty = { innerScope.just(None) },
					ifSome = f,
				)
			},
			innerScope,
		)
	}

	override fun combineWith(other: Context<Context<M, OptionalContext>, @UnsafeVariance A>): OptionalT<M, A> =
		OptionalT(
			innerScope.bind(wrapped) { v ->
				v.fold(
					ifEmpty = { other.asOptionalT.wrapped },
					ifSome = { innerScope.just(v) },
				)
			},
			innerScope,
		)

	override fun equals(other: Any?): Boolean {
		if (this === other) return true
		if (other == null || this::class != other::class) return false
		return wrapped == (other as OptionalT<*, *>).wrapped
	}

	override fun hashCode(): Int = wrapped.hashCode()

	override fun toString(): String {
		return "OptionalT($wrapped)"
	}

	private open class OptionalTScope<M>(
		private val monadScope: Monad.Scope<M>,
	): Scope<M> {
		override fun <A> empty(): OptionalT<M, A> = OptionalT(monadScope.just(None) as Monad<M, Optional<A>>)

		override fun <A> just(a: A): OptionalT<M, A> =
			OptionalT(monadScope.just(Optional.just(a)) as Monad<M, Optional<A>>)

		override fun <A> lift(monad: Monad<M, A>) = OptionalT.lift(monad)
	}

	private open class OptionalTWithZipScope<M>(
		private val monadScope: MonadZip.Scope<M>,
	): OptionalTScope<M>(monadScope), ScopeWithZip<M> {
		override fun <A, B, R> zip(
			ca: Context<Context<M, OptionalContext>, A>,
			cb: Context<Context<M, OptionalContext>, B>,
			f: (A, B) -> R,
		): OptionalT<M, R> {
			return monadScope.zip(ca.asOptionalT, cb.asOptionalT, f)
		}
	}

	interface Scope<M>:
		MonadTrans.Scope<OptionalContext, M>,
		MonadPlus.Scope<Context<M, OptionalContext>> {
		override fun <A> empty(): OptionalT<M, A>
		override fun <A> just(a: A): OptionalT<M, A>
		override fun <A> lift(monad: Monad<M, A>): OptionalT<M, A>
	}

	interface ScopeWithZip<M>:
		Scope<M>,
		MonadZip.Scope<Context<M, OptionalContext>>

	companion object {
		operator fun <M> invoke(monadScope: Monad.Scope<M>): Scope<M> = OptionalTScope(monadScope)
		operator fun <M> invoke(monadScope: MonadZip.Scope<M>): ScopeWithZip<M> = OptionalTWithZipScope(monadScope)

		fun <M, A> lift(monad: Monad<M, A>) = OptionalT(monad.map(::Some))
	}
}

val <M, A> Context<Context<M, OptionalContext>, A>.asOptionalT get() = this as OptionalT<M, A>

fun <M, A> Monad<M, Optional<A>>.transformer() = OptionalT(this)

fun <M: MonadZip<M, *>, A, B, R> zip(opt1: OptionalT<M, A>, opt2: OptionalT<M, B>, f: (A, B) -> R) =
	opt1.zipWith(opt2, f)

fun <M: MonadZip<M, *>, A, B, R> OptionalT<M, A>.zipWith(other: OptionalT<M, B>, f: (A, B) -> R) =
	OptionalT((wrapped as MonadZip<M, Optional<A>>).zipWith(other.wrapped, lift2(f)::invoke))

fun <M, A, B, R> MonadZip.Scope<M>.zip(
	opt1: OptionalT<M, A>,
	opt2: OptionalT<M, B>,
	f: (A, B) -> R,
): OptionalT<M, R> =
	OptionalT(zip(opt1.wrapped, opt2.wrapped, lift2(f)::invoke), this)
