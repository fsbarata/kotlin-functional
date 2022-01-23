package com.github.fsbarata.functional

interface Num {
	fun toDouble(): Double

	operator fun plus(other: Num): Num
	operator fun times(other: Num): Num
	operator fun unaryMinus(): Num = this - this - this
	operator fun minus(other: Num): Num = this + (-other)
	fun abs(): Num
	fun signum(): Num
}

interface Integral: Num {
	fun toInt(): Int

	fun quotRem(): Pair<Integral, Integral>
}

fun Integral.quot() = quotRem().first
fun Integral.rem() = quotRem().second

interface Fractional: Num {
	operator fun div(other: Num): Fractional = times(recip())
	fun recip(): Fractional

	override operator fun plus(other: Num): Fractional
	override operator fun times(other: Num): Fractional
	override operator fun unaryMinus(): Fractional = super.unaryMinus() as Fractional
	override operator fun minus(other: Num): Fractional = super.minus(other) as Fractional
	override fun abs(): Fractional
	override fun signum(): Fractional
}
