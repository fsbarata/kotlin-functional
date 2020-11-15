package com.github.fsbarata.functional.samples

import com.github.fsbarata.functional.control.monad.state.State

/**
 * https://en.wikipedia.org/wiki/Conway%27s_Game_of_Life
 *
 * Why `State` is useful here?
 * 1. Ability to model state updates in the universe, nothing gets executed until we pass the `initial seed`
 * 2. Universe is by definition a State that keeps changing/evolving in each generation. `State` monad
 * allows us to pass that state through.
 */

internal fun gameOfLife(maxGenerations: Int) = gameOfLife(maxGenerations, 0)

private fun gameOfLife(
	maxGenerations: Int,
	currentGeneration: Int = 0,
): State<Universe, Unit> = State.modify<Universe> { it.evolve() }.flatMap {
	if (currentGeneration < maxGenerations - 1) {
		gameOfLife(maxGenerations, currentGeneration + 1)
	} else {
		State.just(it)
	}
}

internal class Universe private constructor(val cells: List<Cell>, val size: Int) {
	constructor(cells: Array<Array<Cell>>): this(cells.flatten(), cells.size)

	fun evolve() =
		Universe(cells.mapIndexed { i, cell -> cell.evolve(cellNeighboursForIndex(i).count { it.isAlive }) }, size)

	private fun cellNeighboursForIndex(index: Int) = neighborCoordinatesOf(index.toX(), index.toY())
		.filter { it.isInBounds() }.map { it.toIndex() }.map { cells[it] }

	private fun neighborCoordinatesOf(x: Int, y: Int) = arrayOf(
		Pair(x - 1, y - 1), Pair(x, y - 1), Pair(x + 1, y - 1), Pair(x - 1, y),
		Pair(x + 1, y), Pair(x - 1, y + 1), Pair(x, y + 1), Pair(x + 1, y + 1)
	)

	fun atPosition(x: Int, y: Int): Cell = cells[(x to y).toIndex()]

	private fun Pair<Int, Int>.isInBounds() = !((first < 0).or(first >= size).or(second < 0).or(second >= size))
	private fun Pair<Int, Int>.toIndex() = second * size + first
	private fun Int.toX() = this % size
	private fun Int.toY() = this / size

	override fun toString(): String {
		return "Universe(cells=$cells, size=$size)"
	}
}

internal data class Cell(val isAlive: Boolean = false)

internal fun Cell.evolve(livingNeighborsCount: Int) = Cell(
	when (livingNeighborsCount) {
		0, 1 -> false
		2 -> isAlive
		3 -> true
		else -> false
	}
)