package com.github.fsbarata.functional

import kotlin.math.absoluteValue
import kotlin.math.sign

interface Num<T: Num<T>>: Comparable<T> {
	fun toDouble(): Double

	operator fun plus(other: T): T
	operator fun times(other: T): T
	operator fun unaryMinus(): T
	operator fun minus(other: T): T = this + (-other)
	fun abs(): T
	fun signum(): Int
}

interface Integral<T: Integral<T>>: Num<T> {
	fun toInt(): Int

	fun divRem(other: T): Pair<T, T> = Pair(div(other), rem(other))
	operator fun div(other: T): T = divRem(other).first
	operator fun rem(other: T): T = divRem(other).second
}


interface Fractional<T: Fractional<T>>: Num<T> {
	operator fun div(other: T): T = times(recip())
	fun recip(): T
}


fun Int.asNum() = IntNum(this)
class IntNum(val int: Int): Num<IntNum>, Integral<IntNum> {
	override fun toDouble() = int.toDouble()
	override operator fun plus(other: IntNum) = IntNum(int + other.int)
	override operator fun times(other: IntNum) = IntNum(int * other.int)
	override operator fun unaryMinus() = IntNum(-int)
	override fun abs() = IntNum(int.absoluteValue)
	override fun signum() = int.sign

	override fun toInt() = int
	override operator fun div(other: IntNum) = IntNum(int / other.int)
	override operator fun rem(other: IntNum) = IntNum(int % other.int)

	override fun compareTo(other: IntNum) = int.compareTo(other.int)
}

fun Double.asNum() = DoubleNum(this)
class DoubleNum(val double: Double): Num<DoubleNum>, Fractional<DoubleNum> {
	override fun toDouble() = double

	override operator fun plus(other: DoubleNum) = DoubleNum(double + other.double)
	override operator fun times(other: DoubleNum) = DoubleNum(double * other.double)
	override operator fun unaryMinus() = DoubleNum(-double)
	override fun abs() = DoubleNum(double.absoluteValue)
	override fun signum() = when {
		double > 0.0 -> 1
		double < 0.0 -> -1
		else -> 0
	}

	override operator fun div(other: DoubleNum) = DoubleNum(double / other.double)
	override fun recip(): DoubleNum = DoubleNum(1.0 / double)

	override fun compareTo(other: DoubleNum) = double.compareTo(other.double)
}
