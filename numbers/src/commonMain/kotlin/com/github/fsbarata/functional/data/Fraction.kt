package com.github.fsbarata.functional.data

import com.github.fsbarata.functional.Fractional
import com.github.fsbarata.functional.Num
import com.github.fsbarata.functional.utils.greatestCommonDenominator
import com.github.fsbarata.io.Serializable
import kotlin.math.absoluteValue
import kotlin.math.sign

typealias Rational = Fraction

class Fraction internal constructor(
	val numerator: Long,
	val denominator: Long,
): Serializable, Num<Fraction>, Comparable<Fraction>, Fractional<Fraction> {
	init {
		require(denominator >= 0)
	}

	override fun equals(other: Any?) = other is Fraction &&
			if (denominator == other.denominator) numerator == other.numerator
			else other.numerator * denominator == numerator * other.denominator

	override fun hashCode() = (denominator / numerator).hashCode()

	override fun toString(): String {
		return "$numerator/$denominator"
	}

	override fun compareTo(other: Fraction) = when {
		denominator == other.denominator -> numerator.compareTo(other.numerator)
		else -> toDouble().compareTo(other.toDouble())
	}

	override fun toDouble(): Double = numerator.toDouble() / denominator.toDouble()

	fun toDoubleOrNull(): Double =
		if (denominator == 0L) Double.NaN
		else toDouble()

	fun reduce(): Fraction = reduce(numerator, denominator)

	override fun abs() = Fraction(numerator.absoluteValue, denominator)
	override fun signum() = numerator.sign

	override operator fun plus(other: Fraction) = when (other.denominator) {
		1L -> from(other.numerator * denominator + numerator, denominator)
		denominator -> from(other.numerator + numerator, denominator)
		else -> from(
			other.numerator * denominator + numerator * other.denominator,
			other.denominator * denominator
		)
	}

	override operator fun minus(other: Fraction) = plus(other.unaryMinus())

	override operator fun unaryMinus() = Fraction(-numerator, denominator)

	override operator fun times(other: Fraction) = Fraction(
		other.numerator * numerator,
		other.denominator * denominator
	)

	override operator fun div(other: Fraction) = times(other.recip())
	override fun recip(): Fraction = from(denominator, numerator)

	operator fun plus(value: Int) = plus(value.toLong())
	operator fun plus(value: Long) = plus(value.toFraction())
	operator fun minus(value: Int) = minus(value.toLong())
	operator fun minus(value: Long) = minus(value.toFraction())
	operator fun times(value: Int) = times(value.toLong())
	operator fun times(value: Long) = Fraction(numerator * value, denominator)
	operator fun div(value: Int) = div(value.toLong())
	operator fun div(value: Long) = from(numerator, denominator * value)

	companion object {
		val NAN = Fraction(0, 0)

		fun parse(text: String): Fraction {
			val terms = text.split("/", limit = 2)
			val numerator = terms[0].toLong()
			val denominator = terms[1].toLong()
			return numerator over denominator
		}

		private fun reduce(numerator: Long, denominator: Long): Fraction {
			val gcd = greatestCommonDenominator(numerator.absoluteValue, denominator.absoluteValue)
			return Fraction(
				if (gcd == 1L) numerator else (numerator / gcd),
				if (gcd == 1L) denominator else (denominator / gcd),
			)
		}

		fun from(numerator: Long, denominator: Long) = when {
			denominator == 0L -> NAN
			denominator < 0L -> Fraction(-numerator, -denominator)
			else -> Fraction(numerator, denominator)
		}
	}
}

operator fun Int.plus(fraction: Fraction) = fraction + this
operator fun Int.minus(fraction: Fraction) = toFraction() - fraction
operator fun Int.times(fraction: Fraction) = fraction * this
operator fun Int.div(fraction: Fraction) = fraction.recip() * this

infix fun Int.over(other: Int) = toLong() over other.toLong()
infix fun Long.over(other: Long) = Fraction.from(this, other)
fun Int.toFraction() = toLong().toFraction()
fun Long.toFraction() = Fraction(this, 1L)

