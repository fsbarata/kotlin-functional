package com.github.fsbarata.functional.control.reader

import com.github.fsbarata.functional.Context
import com.github.fsbarata.functional.assertEquals
import com.github.fsbarata.functional.control.MonadLaws
import kotlin.math.roundToInt
import kotlin.test.Test

private typealias Dependencies = Pair<String, Int>

class ReaderTest:
	MonadLaws<ReaderContext<Dependencies>> {
	override val monadScope = Reader.Scope<Dependencies>()

	override val possibilities: Int = 10
	override fun factory(possibility: Int) =
		Reader<Dependencies, Int> { it.first.sumOf { it.code } * possibility + it.second }

	private val basicDependencies = Pair("ab", -38)

	override fun <A> Context<ReaderContext<Dependencies>, A>.equalTo(other: Context<ReaderContext<Dependencies>, A>) =
		asReader.runReader(basicDependencies) == other.asReader.runReader(basicDependencies)

	@Test
	fun map() {
		val reader = Reader { a: Int -> (a + 2).toString() + 2 }
		assertEquals("52a", reader.map { it + "a" }.runReader(3))
	}

	@Test
	fun flatMap() {
		val reader = Reader { a: Int -> (a + 2).toString() + 2 }
		assertEquals(
			"62a4",
			reader.flatMap { b ->
				Reader { a: Int -> b + "a$a" }
			}.runReader(4)
		)
	}

	@Test
	fun using() {
		val reader = Reader { a: Int -> (a + 2).toString() + 2 }
		assertEquals(
			"52",
			reader.using { b: Double -> (b * 0.3).roundToInt() }.runReader(8.5)
		)
	}

	@Test
	fun ask() {
		assertEquals("11", Reader.ask<String>().runReader("11"))
	}
}