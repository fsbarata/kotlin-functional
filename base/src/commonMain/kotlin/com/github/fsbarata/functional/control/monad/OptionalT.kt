package com.github.fsbarata.functional.control.monad

import com.github.fsbarata.functional.Context
import com.github.fsbarata.functional.control.Monad
import com.github.fsbarata.functional.data.maybe.None
import com.github.fsbarata.functional.data.maybe.Optional
import com.github.fsbarata.functional.data.maybe.OptionalContext


data class OptionalT<M, out A>(val wrapped: Monad<M, Optional<A>>): MonadTrans<OptionalContext, M, A> {
	override val scope = Scope(wrapped.scope)

	inline fun <MM> mapOptionalT(f: (Monad<M, Optional<A>>) -> Monad<MM, Optional<@UnsafeVariance A>>) =
		OptionalT(f(wrapped))

	override fun <B> map(f: (A) -> B): OptionalT<M, B> =
		OptionalT(wrapped.map { it.map(f) })

	override fun <B> bind(f: (A) -> Context<Monad<OptionalContext, M>, B>) = flatMap { f(it).asOptionalT }

	fun <B> flatMap(f: (A) -> OptionalT<M, B>) = OptionalT(
		wrapped.bind { v ->
			v.fold(
				ifEmpty = { wrapped.scope.just(None) },
				ifSome = { f(it).wrapped },
			)
		}
	)

	class Scope<M>(private val monadScope: Monad.Scope<M>): MonadTrans.Scope<OptionalContext, M> {
		override fun <A> just(a: A) =
			OptionalT(monadScope.just(Optional.just(a)))

		override fun <A> lift(monad: Monad<M, A>) =
			OptionalT(monad.map { Optional.just(it) })
	}
}

val <M, A> Context<Monad<OptionalContext, M>, A>.asOptionalT get() = this as OptionalT<M, A>

fun <M, A> Monad.Scope<M>.hoistOptional(optional: Optional<A>) = OptionalT(just(optional))
