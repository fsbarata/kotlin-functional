package com.github.fsbarata.functional.samples

import com.github.fsbarata.functional.control.monad.state.State
import com.github.fsbarata.functional.data.tuple.Tuple2
import java.math.BigDecimal

typealias Balance = BigDecimal
typealias AccountId = String

internal data class Account(val id: AccountId, val balance: Balance)
internal data class Bank(val name: String, val swiftCode: String, val accounts: List<Account> = emptyList())

private fun getAccountBalance(accountId: AccountId) = State.gets<Bank, Balance> { bank ->
	bank.accounts.find { it.id == accountId }?.balance ?: BigDecimal.ZERO
}

private fun withdraw(accountId: AccountId, amount: BigDecimal) = State.modify<Bank> { bank ->
	bank.copy(accounts = bank.accounts.map { acc ->
		if (acc.id == accountId && acc.balance >= amount) {
			acc.copy(balance = acc.balance - amount)
		} else {
			acc
		}
	})
}.map { amount }

private fun deposit(accountId: AccountId, amount: BigDecimal) = State.modify<Bank> { bank ->
	bank.copy(accounts = bank.accounts.map { acc ->
		if (acc.id == accountId) {
			acc.copy(balance = acc.balance + amount)
		} else {
			acc
		}
	})
}.map { amount }

internal fun transferHalfOfTheMoney(from: AccountId, to: AccountId): State<Bank, Balance> =
	getAccountBalance(from).bind { accountBalance ->
		withdraw(from, accountBalance.multiply(BigDecimal("0.5"))).bind { withdrawn ->
			deposit(to, withdrawn)
		}
	}