package com.github.fsbarata.functional.control

import com.github.fsbarata.functional.data.flip
import com.github.fsbarata.functional.data.list.startWithItem

fun <F, A> Applicative<F, A>.replicate(times: Int): Applicative<F, List<A>> =
	if (times <= 0) scope.just(emptyList())
	else lift2(replicate(times - 1), List<A>::startWithItem.flip())
