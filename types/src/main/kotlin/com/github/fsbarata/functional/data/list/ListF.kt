package com.github.fsbarata.functional.data.list

import com.github.fsbarata.functional.control.Applicative
import com.github.fsbarata.functional.control.Context
import com.github.fsbarata.functional.control.Monad
import com.github.fsbarata.functional.control.MonadZip
import com.github.fsbarata.functional.data.Foldable
import com.github.fsbarata.functional.data.Traversable
import com.github.fsbarata.functional.data.compose
import com.github.fsbarata.functional.data.composeForward
import java.io.Serializable

class ListF<A>(
	private val wrapped: List<A>,
): Monad<ListContext, A>,
	MonadZip<ListContext, A>,
	Foldable<A>,
	Traversable<ListContext, A>,
	List<A> by wrapped,
	Serializable {
	override val scope get() = ListF

	@Suppress("OVERRIDE_BY_INLINE")
	override inline fun <B> map(f: (A) -> B) =
		(this as List<A>).map(f).f()

	override fun <B> ap(ff: Applicative<ListContext, (A) -> B>): ListF<B> =
		wrapped.ap(ff.asList).f()

	override fun <B, R> liftA2(f: (A) -> (B) -> R): (Applicative<ListContext, B>) -> ListF<R> =
		wrapped.liftA2(f) compose (Applicative<ListContext, B>::asList) composeForward List<R>::f

	override fun <B> bind(f: (A) -> Context<ListContext, B>) =
		flatMap { f(it).asList }

	inline fun <B> flatMap(f: (A) -> List<B>) =
		(this as List<A>).flatMap(f).f()

	@Suppress("OVERRIDE_BY_INLINE")
	override inline fun <R> foldL(initialValue: R, accumulator: (R, A) -> R): R =
		(this as List<A>).fold(initialValue, accumulator)

	@Suppress("OVERRIDE_BY_INLINE")
	override inline fun <R> foldR(initialValue: R, accumulator: (A, R) -> R): R =
		(this as List<A>).foldRight(initialValue, accumulator)

	override fun <B, R> zipWith(other: MonadZip<ListContext, B>, f: (A, B) -> R): ListF<R> =
		zip(other.asList, f).f()

	override fun <F, B> traverse(
		appScope: Applicative.Scope<F>,
		f: (A) -> Applicative<F, B>,
	): Applicative<F, ListF<B>> =
		(this as List<A>).traverse(appScope, f).map(List<B>::f)

	override fun toString() = wrapped.toString()
	override fun equals(other: Any?) = wrapped == other
	override fun hashCode() = wrapped.hashCode()

	companion object: Monad.Scope<ListContext>, Traversable.Scope<ListContext> {
		fun <A> empty() = emptyList<A>().f()
		override fun <A> just(a: A) = listOf(a).f()
		fun <A> of(vararg items: A) = listOf(*items).f()
	}
}

fun <A> List<A>.f() = ListF(this)
fun <A> List<A>.asMonad(): Monad<ListF<*>, A> = f()
fun <A> List<A>.asFoldable(): Foldable<A> = f()

internal typealias ListContext = ListF<*>

val <A> Context<ListContext, A>.asList: ListF<A>
	get() = this as ListF<A>

