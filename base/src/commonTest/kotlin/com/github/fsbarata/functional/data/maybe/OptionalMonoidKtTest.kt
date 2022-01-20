package com.github.fsbarata.functional.data.maybe

import com.github.fsbarata.functional.assertEquals
import com.github.fsbarata.functional.data.MonoidLaws
import com.github.fsbarata.functional.data.MonoidSemigroupFactory
import com.github.fsbarata.functional.data.StringF
import com.github.fsbarata.functional.data.monoid.concatStringMonoid
import com.github.fsbarata.functional.data.semigroupFactory
import kotlin.test.Test

class OptionalMonoidTest: MonoidLaws<Optional<StringF>>(
	Optional.monoid(),
) {
	override val possibilities: Int = 100
	override fun factory(possibility: Int) = Optional.just(StringF("$possibility"))

	@Test
	fun combines() {
		val semigroupFactory = concatStringMonoid().semigroupFactory()
		with(Optional.monoid<MonoidSemigroupFactory<String>.WrappedMonoid>()) {
			assertEquals(
				Optional.just(semigroupFactory("5")),
				combine(Optional.just(semigroupFactory("5")), Optional.empty())
			)

			assertEquals(
				Optional.just(semigroupFactory("2")),
				combine(Optional.empty(), Optional.just(semigroupFactory("2")))
			)

			assertEquals(
				Optional.just(semigroupFactory("42")),
				combine(Optional.just(semigroupFactory("4")), Optional.just(semigroupFactory("2")))
			)
		}
	}
}