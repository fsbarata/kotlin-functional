package com.github.fsbarata.functional.samples.validation

import java.util.regex.Pattern


private val EMAIL_ADDRESS: Pattern = Pattern.compile(
	"[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
			"\\@" +
			"[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
			"(" +
			"\\." +
			"[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
			")+"
)

actual fun Email.isValidEmail() = EMAIL_ADDRESS.matcher(this).matches()