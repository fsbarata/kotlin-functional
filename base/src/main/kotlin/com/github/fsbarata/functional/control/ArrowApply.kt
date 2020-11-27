package com.github.fsbarata.functional.control

interface ArrowApply<ARR, A, R>: Arrow<ARR, A, R> {
	override val scope: Scope<ARR>

	interface Scope<ARR>: Arrow.Scope<ARR> {
		fun <A, R> app(): ArrowApply<ARR, out Pair<ArrowApply<ARR, A, R>, A>, R>
	}
}
