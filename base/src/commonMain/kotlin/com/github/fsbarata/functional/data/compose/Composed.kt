package com.github.fsbarata.functional.data.compose

import com.github.fsbarata.functional.Context
import com.github.fsbarata.functional.data.Functor

fun <F, G, A, B> Functor<F, Functor<G, A>>.mapComposed(f: (A) -> B): Functor<F, Functor<G, B>> =
	map { g -> g.map(f) }

fun <F, G, A, B> Functor.Scope<F>.mapComposed(
	ca: Context<F, Functor<G, A>>,
	f: (A) -> B,
): Context<F, Functor<G, B>> =
	map(ca) { g -> g.map(f) }

fun <F, G, A, B> Functor.Scope<F>.mapComposed(
	gScope: Functor.Scope<G>,
	ca: Context<F, Context<G, A>>,
	f: (A) -> B,
): Context<F, Context<G, B>> =
	map(ca) { g -> gScope.map(g, f) }

