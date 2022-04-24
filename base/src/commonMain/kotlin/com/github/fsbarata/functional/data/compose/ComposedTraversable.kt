package com.github.fsbarata.functional.data.compose

import com.github.fsbarata.functional.Context
import com.github.fsbarata.functional.control.Applicative
import com.github.fsbarata.functional.data.Traversable

fun <F, G, A, R> Traversable<F, Traversable<G, A>>.foldLComposed(initialValue: R, accumulator: (R, A) -> R): R =
	foldL(initialValue) { acc, ga -> ga.foldL(acc, accumulator) }

fun <F, G, A, R> Traversable<F, Traversable<G, A>>.foldRComposed(initialValue: R, accumulator: (A, R) -> R): R =
	foldR(initialValue) { ga, acc -> ga.foldR(acc, accumulator) }

fun <F, G, A, H, B> Traversable<F, Traversable<G, A>>.traverseComposed(
	appScope: Applicative.Scope<H>,
	f: (A) -> Context<H, B>,
): Context<H, Traversable<F, Traversable<G, B>>> =
	traverse(appScope) { it.traverse(appScope, f) }
