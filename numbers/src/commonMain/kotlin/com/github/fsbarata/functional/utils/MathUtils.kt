package com.github.fsbarata.functional.utils

import kotlin.math.absoluteValue

fun greatestCommonDenominator(a: Long, b: Long): Long =
	greatestCommonDenominatorRec(a.absoluteValue, b.absoluteValue)

private tailrec fun greatestCommonDenominatorRec(a: Long, b: Long): Long =
	if (b == 0L) a
	else greatestCommonDenominatorRec(b, if (b > a) a else a % b)

