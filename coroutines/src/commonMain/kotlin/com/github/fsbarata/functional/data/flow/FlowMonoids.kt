package com.github.fsbarata.functional.data.flow

import com.github.fsbarata.functional.data.monoid
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.onCompletion

fun <A: Any> concatFlowMonoid() = monoid(emptyFlow<A>()) { flow1, flow2 ->
	flow1.onCompletion { if (it == null) emitAll(flow2) }
}
