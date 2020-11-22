package com.github.fsbarata.functional.samples.state

import com.github.fsbarata.functional.control.state.State
import com.github.fsbarata.functional.data.tuple.Tuple2
import java.math.BigDecimal

typealias Balance = BigDecimal
typealias AccountId = String

internal data class Account(val balance: Balance)
internal data class Bank(val name: String, val swiftCode: String, val accounts: Map<AccountId, Account> = emptyMap())

private fun getAccountBalance(accountId: AccountId) = State.gets<Bank, Balance> { bank ->
	bank.accounts[accountId]?.balance ?: BigDecimal.ZERO
}

private fun withdraw(accountId: AccountId, amountToWithdraw: BigDecimal) = State { bank: Bank ->
	val account = bank.accounts[accountId] ?: throw NoSuchElementException()
	val withdrawn = minOf(amountToWithdraw, account.balance)
	Tuple2(
		bank.copy(accounts = bank.accounts + (accountId to account.copy(balance = account.balance - withdrawn))),
		withdrawn
	)
}

private fun deposit(accountId: AccountId, amount: BigDecimal) = State.modify<Bank> { bank ->
	val account = bank.accounts[accountId] ?: throw NoSuchElementException()
	bank.copy(accounts = bank.accounts + (accountId to account.copy(balance = account.balance + amount)))
}

private fun transfer(from: AccountId, to: AccountId, amount: BigDecimal): State<Bank, Balance> =
	withdraw(from, amount).flatMap { withdrawn -> deposit(to, amount).map { withdrawn } }

internal fun transferAQuarterOfTheMoney(from: AccountId, to: AccountId): State<Bank, Balance> =
	getAccountBalance(from).flatMap { accountBalance ->
		transfer(from, to, accountBalance.multiply(BigDecimal("0.25")))
	}