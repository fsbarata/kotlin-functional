package com.github.fsbarata.functional.samples

import com.github.fsbarata.functional.data.list.NonEmptyList
import com.github.fsbarata.functional.data.list.concatNelSemigroup
import com.github.fsbarata.functional.data.list.nelOf
import com.github.fsbarata.functional.data.validation.Validation
import com.github.fsbarata.functional.data.validation.sequence
import java.util.regex.Pattern

typealias Email = String
typealias PhoneNumber = String

sealed class ValidationError {
	object InvalidMail: ValidationError()
	object InvalidPhoneNumber: ValidationError()
}

private fun Email.toValidatedEmail(): Validation<NonEmptyList<ValidationError>, Email> =
	when {
		isValidEmail() -> Validation.Success(this)
		else -> Validation.Failure(nelOf(ValidationError.InvalidMail))
	}

private fun PhoneNumber.toValidatedPhoneNumber(): Validation<NonEmptyList<ValidationError>, PhoneNumber> =
	when {
		isValidPhoneNumber() -> Validation.Success(this)
		else -> Validation.Failure(nelOf(ValidationError.InvalidPhoneNumber))
	}

private val EMAIL_ADDRESS: Pattern = Pattern.compile(
	"[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
			"\\@" +
			"[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
			"(" +
			"\\." +
			"[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
			")+"
)

private fun Email.isValidEmail() = EMAIL_ADDRESS.matcher(this).matches()
private fun PhoneNumber.isValidPhoneNumber() = all { it.isDigit() }

fun validateData(
	mail: String,
	phoneNumber: String,
) = concatNelSemigroup<ValidationError>().sequence(
	mail.toValidatedEmail(),
	phoneNumber.toValidatedPhoneNumber(),
	::Pair
)

private fun NonEmptyList<ValidationError>.handleInvalid() = map {
	handleInvalidField(it)
}

private fun handleInvalidField(validationError: ValidationError): String =
	when (validationError) {
		ValidationError.InvalidMail -> "Invalid email"
		ValidationError.InvalidPhoneNumber -> "Invalid phone number"
	}