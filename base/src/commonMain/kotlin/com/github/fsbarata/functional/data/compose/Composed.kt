package com.github.fsbarata.functional.data.compose

import com.github.fsbarata.functional.Context
import com.github.fsbarata.functional.control.Applicative
import com.github.fsbarata.functional.control.Lift1
import com.github.fsbarata.functional.control.Lift2
import com.github.fsbarata.functional.data.Functor
import com.github.fsbarata.functional.data.Traversable

fun <F, G, A, B> Functor<F, Functor<G, A>>.mapComposed(f: (A) -> B): Functor<F, Functor<G, B>> =
	map { ga -> ga.map(f) }

fun <F, G, A, B> Functor<F, Context<G, A>>.mapComposed(
	gScope: Functor.Scope<G>,
	f: (A) -> B,
): Functor<F, Context<G, B>> =
	map { ga -> gScope.map(ga, f) }

fun <F, G, A, B> Functor.Scope<F>.mapComposed(
	fga: Context<F, Functor<G, A>>,
	f: (A) -> B,
): Context<F, Functor<G, B>> =
	map(fga) { ga -> ga.map(f) }

fun <F, G, A, B> Functor.Scope<F>.mapComposed(
	gScope: Functor.Scope<G>,
	fga: Context<F, Context<G, A>>,
	f: (A) -> B,
): Context<F, Context<G, B>> =
	map(fga) { ga -> gScope.map(ga, f) }

fun <F, G, A, R> Lift1<A, R>.fmapComposed(fga: Functor<F, Functor<G, A>>): Functor<F, Functor<G, R>> =
	fga.mapComposed(f)

fun <F, G, A, R> Lift1<A, R>.fmapComposed(
	gScope: Functor.Scope<G>,
	fga: Functor<F, Context<G, A>>,
): Functor<F, Context<G, R>> =
	fga.mapComposed(gScope, f)

fun <F, G, A, R> Lift1<A, R>.fmapComposed(
	fScope: Functor.Scope<F>,
	fga: Context<F, Functor<G, A>>,
): Context<F, Functor<G, R>> = fScope.mapComposed(fga, f)

fun <F, G, A, R> Lift1<A, R>.fmapComposed(
	fScope: Functor.Scope<F>,
	gScope: Functor.Scope<G>,
	fga: Context<F, Context<G, A>>,
): Context<F, Context<G, R>> = fScope.mapComposed(gScope, fga, f)

fun <F, G, A, B, R> Applicative<F, Applicative<G, A>>.lift2Composed(
	fgb: Applicative<F, Applicative<G, B>>,
	f: (A, B) -> R,
): Applicative<F, Applicative<G, R>> = lift2(fgb) { ga, gb -> ga.lift2(gb, f) }

fun <F, G, A, B, R> Applicative<F, Context<G, A>>.lift2Composed(
	gScope: Applicative.Scope<G>,
	fgb: Context<F, Context<G, B>>,
	f: (A, B) -> R,
): Applicative<F, Context<G, R>> = lift2(fgb) { ga, gb -> gScope.lift2(ga, gb, f) }

fun <F, G, A, B, R> Applicative.Scope<F>.lift2Composed(
	fga: Context<F, Applicative<G, A>>,
	fgb: Context<F, Applicative<G, B>>,
	f: (A, B) -> R,
): Context<F, Applicative<G, R>> = lift2(fga, fgb) { ga, gb -> ga.lift2(gb, f) }

fun <F, G, A, B, R> Applicative.Scope<F>.lift2Composed(
	gScope: Applicative.Scope<G>,
	fga: Context<F, Context<G, A>>,
	fgb: Context<F, Context<G, B>>,
	f: (A, B) -> R,
): Context<F, Context<G, R>> = lift2(fga, fgb) { ga, gb -> gScope.lift2(ga, gb, f) }

fun <F, G, A, B, R> Lift2<A, B, R>.appComposed(
	fga: Applicative<F, Applicative<G, A>>,
	fgb: Applicative<F, Applicative<G, B>>,
): Applicative<F, Applicative<G, R>> = fga.lift2Composed(fgb, f)

fun <F, G, A, B, R> Lift2<A, B, R>.appComposed(
	gScope: Applicative.Scope<G>,
	fga: Applicative<F, Context<G, A>>,
	fgb: Applicative<F, Context<G, B>>,
): Applicative<F, Context<G, R>> = fga.lift2Composed(gScope, fgb, f)

fun <F, G, A, B, R> Lift2<A, B, R>.appComposed(
	fScope: Applicative.Scope<F>,
	fga: Context<F, Applicative<G, A>>,
	fgb: Context<F, Applicative<G, B>>,
): Context<F, Applicative<G, R>> = fScope.lift2Composed(fga, fgb, f)

fun <F, G, A, B, R> Lift2<A, B, R>.appComposed(
	fScope: Applicative.Scope<F>,
	gScope: Applicative.Scope<G>,
	fga: Context<F, Context<G, A>>,
	fgb: Context<F, Context<G, B>>,
): Context<F, Context<G, R>> = fScope.lift2Composed(gScope, fga, fgb, f)



fun <F, G, A, R> Traversable<F, Traversable<G, A>>.foldLComposed(initialValue: R, accumulator: (R, A) -> R): R =
	foldL(initialValue) { acc, ga -> ga.foldL(acc, accumulator) }

fun <F, G, A, R> Traversable<F, Traversable<G, A>>.foldRComposed(initialValue: R, accumulator: (A, R) -> R): R =
	foldR(initialValue) { ga, acc -> ga.foldR(acc, accumulator) }

fun <F, G, A, H, B> Traversable<F, Traversable<G, A>>.traverseComposed(
	appScope: Applicative.Scope<H>,
	f: (A) -> Context<H, B>,
): Context<H, Traversable<F, Traversable<G, B>>> =
	traverse(appScope) { it.traverse(appScope, f) }
