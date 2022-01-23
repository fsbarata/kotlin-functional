package com.github.fsbarata.functional.control.monad

import com.github.fsbarata.functional.Context
import com.github.fsbarata.functional.control.Monad
import com.github.fsbarata.functional.control.MonadPlus
import com.github.fsbarata.functional.data.maybe.None
import com.github.fsbarata.functional.data.maybe.Optional
import com.github.fsbarata.functional.data.maybe.OptionalContext
import com.github.fsbarata.functional.data.maybe.Some


@Suppress("OVERRIDE_BY_INLINE", "NOTHING_TO_INLINE")
data class OptionalT<M, out A>(
	val wrapped: Monad<M, Optional<A>>,
): MonadTrans<OptionalContext, M, A> {
	override val scope = Scope(wrapped.scope)

	inline fun <MM> mapOptionalT(f: (Monad<M, Optional<A>>) -> Monad<MM, Optional<@UnsafeVariance A>>) =
		OptionalT(f(wrapped))

	override fun <B> map(f: (A) -> B): OptionalT<M, B> =
		OptionalT(wrapped.map { it.map(f) })

	override fun <B> bind(f: (A) -> Context<Monad<M, OptionalContext>, B>) =
		flatMap { f(it).asOptionalT }

	fun <B> flatMap(f: (A) -> OptionalT<M, B>) = OptionalT(
		wrapped.bind { v ->
			v.fold(
				ifEmpty = { wrapped.scope.just(None) },
				ifSome = { f(it).wrapped },
			)
		}
	)

	class Scope<M>(
		private val monadScope: Monad.Scope<M>,
	): MonadTrans.Scope<OptionalContext, M> {
		override fun <A> just(a: A) =
			OptionalT(monadScope.just(Optional.just(a)))

		override inline fun <A> lift(monad: Monad<M, A>) = OptionalT.lift(monad)
	}

	companion object {
		operator fun <M> invoke(monadScope: Monad.Scope<M>) = Scope(monadScope)

		fun <M, A> lift(monad: Monad<M, A>) = OptionalT(monad.map(::Some))
	}
}

val <M, A> Context<Monad<M, OptionalContext>, A>.asOptionalT get() = this as OptionalT<M, A>

fun <M, A> Monad.Scope<M>.hoistOptional(optional: Optional<A>) = OptionalT(just(optional))
