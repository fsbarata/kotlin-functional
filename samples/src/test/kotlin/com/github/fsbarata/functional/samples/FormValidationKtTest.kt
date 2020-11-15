package com.github.fsbarata.functional.samples

import com.github.fsbarata.functional.data.list.NonEmptyList
import com.github.fsbarata.functional.data.list.nelOf
import junit.framework.TestCase.assertEquals
import org.junit.Test

class FormValidationKtTest {

	@Test
	fun `validation accumulates errors`() {
		var actual: NonEmptyList<ValidationError>? = null
		val expected = nelOf(ValidationError.InvalidMail, ValidationError.InvalidPhoneNumber)
		validateData("a@", "123-").fold(
			ifFailure = { actual = it },
			ifSuccess = { throw Exception() })

		assertEquals(expected, actual)
	}

	@Test
	fun `validation goes towards ifFailure if one of the validation fails`() {
		var actual: NonEmptyList<ValidationError>? = null
		val expected = nelOf(ValidationError.InvalidMail)
		validateData("a@", "123").fold(
			ifFailure = { actual = it },
			ifSuccess = { throw Exception() })

		assertEquals(expected, actual)
	}

	@Test
	fun `validation goes towards ifSuccess if both elements are valid`() {
		var actual: Pair<String, String>? = null
		val mail = "a@gmail.com"
		val phoneNumber = "123"
		val expected = mail to phoneNumber
		validateData(mail, phoneNumber).fold(
			ifFailure = { throw Exception() },
			ifSuccess = { actual = it })

		assertEquals(expected, actual)
	}
}