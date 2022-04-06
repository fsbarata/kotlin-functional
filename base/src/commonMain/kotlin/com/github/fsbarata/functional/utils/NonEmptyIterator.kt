package com.github.fsbarata.functional.utils

import com.github.fsbarata.functional.data.list.ListF
import com.github.fsbarata.functional.data.list.NonEmptyList
import com.github.fsbarata.functional.data.set.NonEmptySet
import com.github.fsbarata.functional.data.set.SetF

internal fun <A> nonEmptyIterator(head: A, tail: Iterator<A>) = object: Iterator<A> {
	private var begin: Boolean = true

	override fun hasNext() = begin || tail.hasNext()

	override fun next(): A {
		if (begin) {
			begin = false
			return head
		}
		return tail.next()
	}
}

internal fun <A> nonEmptyIterator(head: Iterator<A>, tail: A) = object: Iterator<A> {
	private var hasNext: Boolean = true

	override fun hasNext() = hasNext

	override fun next(): A =
		if (head.hasNext()) head.next()
		else {
			hasNext = false
			tail
		}
}

internal fun <A> Iterator<A>.toNelUnsafe() = NonEmptyList.of(next(), ListF.fromSequence(asSequence()))
fun <A> Iterator<A>.toNel(): NonEmptyList<A>? =
	if (!hasNext()) null else toNelUnsafe()

internal fun <A> Iterator<A>.toNesUnsafe() = NonEmptySet.of(next(), SetF.fromSequence(asSequence()))
fun <A> Iterator<A>.toNes(): NonEmptySet<A>? =
	if (!hasNext()) null else toNesUnsafe()
