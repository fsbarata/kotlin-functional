package com.github.fsbarata.functional.data.list

import com.github.fsbarata.functional.Context
import com.github.fsbarata.functional.control.*
import com.github.fsbarata.functional.data.*
import com.github.fsbarata.functional.data.maybe.Optional
import java.io.Serializable

@Suppress("OVERRIDE_BY_INLINE")
class ListF<A>(private val wrapped: List<A>): List<A> by wrapped,
	Serializable,
	MonadZip<ListContext, A>,
	MonadPlus<ListContext, A>,
	Traversable<ListContext, A>,
	Semigroup<ListF<A>> {
	override val scope get() = ListF

	override inline fun <B> map(f: (A) -> B) =
		(this as List<A>).map(f).f()

	override infix fun <B> ap(ff: Functor<ListContext, (A) -> B>): ListF<B> =
		wrapped.ap(ff.asList).f()

	override inline fun <B, R> lift2(fb: Functor<ListContext, B>, f: (A, B) -> R): ListF<R> =
		(this as List<A>).lift2(fb.asList, f).f()

	override inline infix fun <B> bind(f: (A) -> Context<ListContext, B>) =
		flatMap { f(it).asList }

	inline fun <B> flatMap(f: (A) -> List<B>) =
		(this as List<A>).flatMap(f).f()

	override inline fun filter(predicate: (A) -> Boolean) =
		(this as List<A>).filter(predicate).f()

	override inline fun partition(predicate: (A) -> Boolean): Pair<ListF<A>, ListF<A>> {
		val p = (this as List<A>).partition(predicate)
		return Pair(p.first.f(), p.second.f())
	}

	override inline fun <B: Any> mapNotNull(f: (A) -> B?) =
		(this as List<A>).mapNotNull(f).f()

	override inline fun <B: Any> mapNotNone(f: (A) -> Optional<B>) =
		(this as List<A>).mapNotNull { f(it).orNull() }.f()

	override inline fun <R> foldL(initialValue: R, accumulator: (R, A) -> R): R =
		(this as List<A>).fold(initialValue, accumulator)

	override inline fun <R> foldR(initialValue: R, accumulator: (A, R) -> R): R =
		(this as List<A>).foldRight(initialValue, accumulator)

	override inline fun <M> foldMap(monoid: Monoid<M>, f: (A) -> M): M =
		(this as List<A>).foldMap(monoid, f)

	override inline fun <B, R> zipWith(other: MonadZip<ListContext, B>, f: (A, B) -> R): ListF<R> =
		zip(other.asList, f).f()

	override inline fun <F, B> traverse(
		appScope: Applicative.Scope<F>,
		f: (A) -> Functor<F, B>,
	): Functor<F, ListF<B>> =
		(this as List<A>).traverse(appScope, f).map(List<B>::f)

	override fun associateWith(other: Context<ListContext, A>) =
		combineWith(other.asList)

	override fun combineWith(other: ListF<A>) =
		ListF(wrapped + other.wrapped)

	override fun toString() = wrapped.toString()
	override fun equals(other: Any?) = wrapped == other
	override fun hashCode() = wrapped.hashCode()

	companion object:
		MonadPlus.Scope<ListContext>,
		Traversable.Scope<ListContext> {
		override fun <A> empty() = emptyList<A>().f()
		override fun <A> just(a: A) = listOf(a).f()
		fun <A> of(vararg items: A) = listOf(*items).f()

		fun <A> monoid() = monoid(empty<A>())

		@Deprecated("Can be simplified", replaceWith = ReplaceWith("f()"))
		override fun <A> fromList(list: List<A>) = list.f()

		override fun <A> fromOptional(optional: Optional<A>) =
			optional.maybe(empty(), ::just)
	}
}

fun <A> List<A>.f() = ListF(this)
fun <A> List<A>.asFoldable(): Foldable<A> = f()
fun <A, R> List<A>.f(block: ListF<A>.() -> Context<ListContext, R>) =
	f().block().asList

internal typealias ListContext = ListF<*>

val <A> Context<ListContext, A>.asList: ListF<A>
	get() = this as ListF<A>


operator fun <A, B, R> Lift2<A, B, R>.invoke(
	list1: ListF<A>,
	list2: ListF<B>,
): ListF<R> = app(list1, list2).asList

operator fun <A, B, C, R> Lift3<A, B, C, R>.invoke(
	list1: ListF<A>,
	list2: ListF<B>,
	list3: ListF<C>,
): ListF<R> = app(list1, list2, list3).asList

operator fun <A, B, C, D, R> Lift4<A, B, C, D, R>.invoke(
	list1: ListF<A>,
	list2: ListF<B>,
	list3: ListF<C>,
	list4: ListF<D>,
): ListF<R> = app(list1, list2, list3, list4).asList
