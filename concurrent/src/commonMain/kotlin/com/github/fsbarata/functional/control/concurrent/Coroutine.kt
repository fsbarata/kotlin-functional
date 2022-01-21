package com.github.fsbarata.functional.control.concurrent

import com.github.fsbarata.functional.Context
import com.github.fsbarata.functional.control.Monad
import com.github.fsbarata.functional.control.MonadMapper
import com.github.fsbarata.functional.data.Functor
import com.github.fsbarata.functional.data.FunctorMapper
import com.github.fsbarata.functional.data.compose
import com.github.fsbarata.functional.data.either.Either

typealias CoroutineStepResult<M, S, R> = Either<Functor<S, Coroutine<M, S, R>>, R>

class Coroutine<M, S, R>(
	private val monadScope: Monad.Scope<M>,
	val resume: () -> Monad<M, CoroutineStepResult<M, S, R>>,
): Monad<CoroutineContext<M, S>, R> {
	override val scope = Scope<M, S>(monadScope)

	override fun <B> map(f: (R) -> B): Coroutine<M, S, B> =
		Coroutine(monadScope) { resume().map { result -> result.bimap({ functor -> functor.map { it.map(f) } }, f) } }

	override fun <B> bind(f: (R) -> Context<CoroutineContext<M, S>, B>): Coroutine<M, S, B> =
		flatMap { f(it).asCoroutine }

	fun <B> flatMap(f: (R) -> Coroutine<M, S, B>): Coroutine<M, S, B> =
		Coroutine(monadScope) {
			resume().bind { result ->
				when (result) {
					is Either.Right<R> -> f(result.value).resume()
					is Either.Left<Functor<S, Coroutine<M, S, R>>> ->
						monadScope.just(Either.Left(result.value.map { it.bind(f) }))
				}
			}
		}

	fun <MM> mapMonad(otherMonadScope: Monad.Scope<MM>, f: MonadMapper<M, MM>): Coroutine<MM, S, R> {
		return Coroutine(otherMonadScope) {
			f(resume()).map { result ->
				when (result) {
					is Either.Right<R> -> result
					is Either.Left<Functor<S, Coroutine<M, S, R>>> ->
						Either.Left(result.value.map { it.asCoroutine.mapMonad(otherMonadScope, f) })
				}
			}
		}
	}

	fun <SS> mapSuspension(f: FunctorMapper<S, SS>): Coroutine<M, SS, R> {
		return Coroutine(monadScope) {
			resume().map { result ->
				when (result) {
					is Either.Right<R> -> result
					is Either.Left<Functor<S, Coroutine<M, S, R>>> ->
						Either.Left(f(result.value.map { it.asCoroutine.mapSuspension(f) }))
				}
			}
		}
	}

	fun mapFirstSuspension(f: FunctorMapper<S, S>): Coroutine<M, S, R> {
		return Coroutine(monadScope) {
			resume().map { result ->
				when (result) {
					is Either.Right<R> -> result
					is Either.Left<Functor<S, Coroutine<M, S, R>>> ->
						Either.Left(f(result.value))
				}
			}
		}
	}

	fun bounce(f: (Functor<S, Coroutine<M, S, R>>) -> Coroutine<M, S, R>): Coroutine<M, S, R> {
		val result = resume()
		return scope.lift(result).bind { it.fold(ifLeft = f, ifRight = scope::just).asCoroutine }
	}

	fun pogoStick(f: (Functor<S, Coroutine<M, S, R>>) -> Coroutine<M, S, R>): Monad<M, R> {
		fun loop(c: Coroutine<M, S, R>): Monad<M, R> =
			c.resume().bind { it.fold(ifLeft = ::loop compose f, ifRight = monadScope::just) }

		return loop(this)
	}

	class Scope<M, S>(private val monadScope: Monad.Scope<M>): Monad.Scope<Coroutine<M, S, *>> {
		override fun <A> just(a: A): Coroutine<M, S, A> =
			Coroutine(monadScope) { monadScope.just(Either.Right(a)) }

		fun <A> suspend(s: Functor<S, Coroutine<M, S, A>>): Coroutine<M, S, A> =
			Coroutine(monadScope) { monadScope.just(Either.Left(s)) }

		fun <A> lift(m: Monad<M, A>) =
			Coroutine<M, S, A>(monadScope) { m.map { Either.Right(it) } }
	}
}

fun <M, R> Coroutine<M, Nothing, R>.runCoroutine(): Monad<M, R> =
	resume().map { either -> either.fold(ifLeft = { throw IllegalStateException() }, ifRight = { it }) }

typealias CoroutineContext<M, F> = Coroutine<M, F, *>

val <M, F, R> Context<CoroutineContext<M, F>, R>.asCoroutine get() = this as Coroutine<M, F, R>
