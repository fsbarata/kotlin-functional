package com.github.fsbarata.functional.samples

import com.github.fsbarata.functional.samples.state.Account
import com.github.fsbarata.functional.samples.state.Bank
import com.github.fsbarata.functional.samples.state.transferAQuarterOfTheMoney
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
			accounts = listOf(
				Account(fromAccountId, BigDecimal("200")),
				Account(toAccountId, BigDecimal("20"))
			)
		)
		val (bankResult, amount) = transferAQuarterOfTheMoney(from = fromAccountId, to = toAccountId).runState(bank)
		assertThat(amount, Matchers.comparesEqualTo(BigDecimal("50")))
		assertThat(bankResult.accounts.find { it.id == fromAccountId }?.balance,
			Matchers.comparesEqualTo(BigDecimal("150")))
		assertThat(bankResult.accounts.find { it.id == toAccountId }?.balance,
			Matchers.comparesEqualTo(BigDecimal("70")))
	}

	companion object {
		const val fromAccountId = "101931"
		const val toAccountId = "101932"
	}
}