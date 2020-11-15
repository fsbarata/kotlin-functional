package com.github.fsbarata.functional.data

import com.github.fsbarata.functional.data.list.nelOf
import com.github.fsbarata.functional.data.string.StringF
import org.junit.Assert.assertEquals
import org.junit.Test

class SemigroupsKtTest {
	@Test
	fun sconcat() {
		assertEquals(
			StringF("5ag2"),
			nelOf(StringF("5a"), StringF("g"), StringF(""), StringF("2")).sconcat(),
		)
	}
}