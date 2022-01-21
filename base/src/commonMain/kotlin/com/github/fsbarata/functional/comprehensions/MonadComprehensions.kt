package com.github.fsbarata.functional.comprehensions

import com.github.fsbarata.functional.control.Monad

interface MonadComprehensionScope<M> {
	fun <A> Monad<M, A>.bind(): A
}

@Suppress("unused")
fun <M, A> MonadComprehensionScope<M>.switch(monad: Monad<M, A>): Nothing {
	throw ComprehensionSignal(monad)
}

inline fun <M, A> MonadComprehensionScope<M>.invokeImpl(
	just: (A) -> Monad<M, A>,
	f: MonadComprehensionScope<M>.() -> A,
): Monad<M, A> {
	try {
		return just(f())
	} catch (signal: ComprehensionSignal) {
		@Suppress("UNCHECKED_CAST")
		return signal.obj as Monad<M, A>
	}
}

class ComprehensionSignal(val obj: Any): Throwable()
