package com.github.fsbarata.functional.samples.validation

import com.github.fsbarata.functional.data.list.NonEmptyList
import com.github.fsbarata.functional.data.validation.Validation
import com.github.fsbarata.functional.data.validation.lift2

typealias Email = String
typealias PhoneNumber = String

sealed class ValidationError {
	object InvalidMail: ValidationError()
	object InvalidPhoneNumber: ValidationError()
}

private fun Email.toValidatedEmail(): Validation<ValidationError, Email> =
	when {
		isValidEmail() -> Validation.Success(this)
		else -> Validation.Failure(ValidationError.InvalidMail)
	}

private fun PhoneNumber.toValidatedPhoneNumber(): Validation<ValidationError, PhoneNumber> =
	when {
		isValidPhoneNumber() -> Validation.Success(this)
		else -> Validation.Failure(ValidationError.InvalidPhoneNumber)
	}

expect fun Email.isValidEmail(): Boolean
private fun PhoneNumber.isValidPhoneNumber() = all { it.isDigit() }

fun validateData(
	mail: String,
	phoneNumber: String,
) = lift2(
	mail.toValidatedEmail().toValidationNel(),
	phoneNumber.toValidatedPhoneNumber().toValidationNel(),
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
