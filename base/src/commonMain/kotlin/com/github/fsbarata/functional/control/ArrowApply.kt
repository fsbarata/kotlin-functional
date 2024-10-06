package com.github.fsbarata.functional.control

object ArrowApply {
	interface Scope<ARR>: Arrow.Scope<ARR> {
		fun <A, R> app(): Arrow<ARR, out Pair<Arrow<ARR, A, R>, A>, R>
	}
}
