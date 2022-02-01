package com.github.fsbarata.functional.data

import com.github.fsbarata.functional.Fractional
import com.github.fsbarata.functional.Integral
import com.github.fsbarata.functional.Num
import com.github.fsbarata.io.Serializable
import kotlin.math.absoluteValue
import kotlin.math.sign


fun Int.asNum() = IntNum(this)
class IntNum(val int: Int): Num<IntNum>, Integral<IntNum>, Serializable {
	override fun toString() = int.toString()

	override fun equals(other: Any?) = other is IntNum && int == other.int
	override fun hashCode() = int.hashCode()
	override fun compareTo(other: IntNum) = int.compareTo(other.int)

	override fun toDouble() = int.toDouble()
	override operator fun plus(other: IntNum) = IntNum(int + other.int)
	override operator fun times(other: IntNum) = IntNum(int * other.int)
	override operator fun unaryMinus() = IntNum(-int)
	override fun abs() = IntNum(int.absoluteValue)
	override fun signum() = int.sign

	override fun toInt() = int
	override fun toLong() = int.toLong()
	override operator fun div(other: IntNum) = IntNum(int / other.int)
	override operator fun rem(other: IntNum) = IntNum(int % other.int)
}

fun Long.asNum() = LongNum(this)
class LongNum(val long: Long): Num<LongNum>, Integral<LongNum>, Serializable {
	override fun toString() = long.toString()

	override fun equals(other: Any?) = other is LongNum && long == other.long
	override fun hashCode() = long.hashCode()
	override fun compareTo(other: LongNum) = long.compareTo(other.long)

	override fun toDouble() = long.toDouble()
	override operator fun plus(other: LongNum) = LongNum(long + other.long)
	override operator fun times(other: LongNum) = LongNum(long * other.long)
	override operator fun unaryMinus() = LongNum(-long)
	override fun abs() = LongNum(long.absoluteValue)
	override fun signum() = long.sign

	override fun toInt() = long.toInt()
	override fun toLong() = long
	override operator fun div(other: LongNum) = LongNum(long / other.long)
	override operator fun rem(other: LongNum) = LongNum(long % other.long)
}

fun Double.asNum() = DoubleNum(this)
class DoubleNum(val double: Double): Num<DoubleNum>, Fractional<DoubleNum>, Serializable {
	override fun toString() = double.toString()

	override fun equals(other: Any?) = other is DoubleNum && double == other.double
	override fun hashCode() = double.hashCode()
	override fun compareTo(other: DoubleNum) = double.compareTo(other.double)

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
}
