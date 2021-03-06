package com.github.fsbarata.functional.samples

import com.github.fsbarata.functional.data.list.NonEmptyList
import com.github.fsbarata.functional.data.list.nelOf
import com.github.fsbarata.functional.samples.validation.ValidationError
import com.github.fsbarata.functional.samples.validation.validateData
import junit.framework.TestCase.assertEquals
import org.junit.Test

class FormValidationKtTest {

	@Test
	fun `validation accumulates errors`() {
		val expected = nelOf(ValidationError.InvalidMail, ValidationError.InvalidPhoneNumber)
		val actual = validateData("a@", "123-").fold(
			ifFailure = { it },
			ifSuccess = { throw Exception() })

		assertEquals(expected, actual)
	}

	@Test
	fun `validation goes towards ifFailure if one of the validation fails`() {
		val expected = nelOf(ValidationError.InvalidMail)
		val actual = validateData("a@", "123").fold(
			ifFailure = { it },
			ifSuccess = { throw Exception() })

		assertEquals(expected, actual)
	}

	@Test
	fun `validation goes towards ifSuccess if both elements are valid`() {
		val mail = "a@gmail.com"
		val phoneNumber = "123"
		val expected = mail to phoneNumber
		val actual = validateData(mail, phoneNumber).fold(
			ifFailure = { throw Exception() },
			ifSuccess = { it })

		assertEquals(expected, actual)
	}
}