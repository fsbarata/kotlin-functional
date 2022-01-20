package com.github.fsbarata.functional.samples.state

import com.github.fsbarata.functional.samples.state.Cell
import com.github.fsbarata.functional.samples.state.Universe
import com.github.fsbarata.functional.samples.state.gameOfLife
import junit.framework.TestCase.assertFalse
import org.junit.Test

class GameOfLifeKtTest {

	@Test
	fun `universe evolution`() {
		val initialSeed = Universe(
			arrayOf(
				arrayOf(Cell(), Cell(), Cell()),
				arrayOf(Cell(), Cell(true), Cell()),
				arrayOf(Cell(), Cell(), Cell(true))
			)
		)

		gameOfLife(1).runState(initialSeed).x.run {
			assertFalse(cellAt(1, 1).isAlive)
			assertFalse(cellAt(2, 2).isAlive)
		}
	}
}

private fun Universe.cellAt(x: Int, y: Int): Cell = atPosition(x, y)