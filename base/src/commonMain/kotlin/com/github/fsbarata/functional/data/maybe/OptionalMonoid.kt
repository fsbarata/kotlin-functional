package com.github.fsbarata.functional.data.maybe

import com.github.fsbarata.functional.data.Monoid
import com.github.fsbarata.functional.data.Semigroup
import com.github.fsbarata.functional.data.monoidOf

fun <A> optionalMonoid(semigroupScope: Semigroup.Scope<A>): Monoid<Optional<A>> =
	monoidOf(Optional.empty()) { a1, a2 ->
		a1.fold(
			ifEmpty = { a2 },
			ifSome = { a -> a2.map { semigroupScope.concat(a, it) } orOptional a1 }
		)
	}


fun <A: Any> nullableMonoid(semigroupScope: Semigroup.Scope<A>): Monoid<A?> =
	monoidOf(null) { a1, a2 ->
		semigroupScope.concat(
			a1 ?: return@monoidOf a2,
			a2 ?: return@monoidOf a1,
		)
	}

