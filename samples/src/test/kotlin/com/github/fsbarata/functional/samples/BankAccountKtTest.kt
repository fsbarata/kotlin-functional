package com.github.fsbarata.functional.samples

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers
import org.junit.Test
import java.math.BigDecimal

class BankAccountKtTest {

	@Test
	fun `transfer half the money from one account to other`() {
		val bank = Bank(
			name = "Central Bank",
			swiftCode = "CBESMMX",
			accounts = listOf(
				Account(fromAccountId, BigDecimal("200")),
				Account(toAccountId, BigDecimal("0"))
			)
		)
		val (bankResult, amount) = transferHalfOfTheMoney(from = fromAccountId, to = toAccountId).runState(bank)
		assertThat(amount, Matchers.comparesEqualTo(BigDecimal("100")))
		assertThat(bankResult.accounts.find { it.id == fromAccountId }?.balance,
			Matchers.comparesEqualTo(BigDecimal("100")))
		assertThat(bankResult.accounts.find { it.id == toAccountId }?.balance,
			Matchers.comparesEqualTo(BigDecimal("100")))
	}

	companion object {
		const val fromAccountId = "101931"
		const val toAccountId = "101932"
	}
}