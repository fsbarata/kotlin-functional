package com.github.fsbarata.functional.samples.state

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers
import org.junit.Test
import java.math.BigDecimal

class BankAccountKtTest {

	@Test
	fun `transfer a quarter the money from one account to other`() {
		val bank = Bank(
			name = "Central Bank",
			swiftCode = "CBESMMX",
			accounts = mapOf(
				fromAccountId to Account(BigDecimal("200")),
				toAccountId to Account(BigDecimal("20"))
			)
		)
		val (bankResult, amount) = transferAQuarterOfTheMoney(from = fromAccountId, to = toAccountId)(bank)
		assertThat(amount, Matchers.comparesEqualTo(BigDecimal("50")))
		assertThat(bankResult.accounts[fromAccountId]?.balance,
			Matchers.comparesEqualTo(BigDecimal("150")))
		assertThat(bankResult.accounts[toAccountId]?.balance,
			Matchers.comparesEqualTo(BigDecimal("70")))
	}

	companion object {
		const val fromAccountId = "101931"
		const val toAccountId = "101932"
	}
}