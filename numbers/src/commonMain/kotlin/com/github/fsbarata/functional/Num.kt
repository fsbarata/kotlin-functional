package com.github.fsbarata.functional

interface Num<T: Num<T>>: Comparable<T> {
	fun toDouble(): Double

	operator fun plus(other: T): T
	operator fun times(other: T): T
	operator fun unaryMinus(): T
	operator fun minus(other: T): T = this + (-other)
	fun abs(): T
	fun signum(): Int
}

fun Num<*>.isPositive() = signum() > 0
fun Num<*>.isZero() = signum() == 0
fun Num<*>.isNegative() = signum() < 0

interface Integral<T: Integral<T>>: Num<T> {
	fun toInt(): Int
	fun toLong(): Long

	fun divRem(other: T): Pair<T, T> = Pair(div(other), rem(other))
	operator fun div(other: T): T = divRem(other).first
	operator fun rem(other: T): T = divRem(other).second
}


interface Fractional<T: Fractional<T>>: Num<T> {
	operator fun div(other: T): T = times(recip())
	fun recip(): T
}
