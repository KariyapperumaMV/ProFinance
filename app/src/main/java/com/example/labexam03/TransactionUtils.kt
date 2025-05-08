package com.example.labexam03

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken
import androidx.core.content.edit

object TransactionUtils {
    private const val PREFS_NAME = "TransactionPrefs"
    private const val TRANSACTION_KEY = "transactions"

    //for adding new transaction
    fun saveTransaction(context: Context, transaction: Transaction) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val gson = Gson()
        val existing = getTransactions(context).toMutableList()
        existing.add(transaction)
        val json = gson.toJson(existing)
        prefs.edit { putString(TRANSACTION_KEY, json) }
    }

    //to retrieve full transaction history
    fun getTransactions(context: Context): List<Transaction> {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val json = prefs.getString(TRANSACTION_KEY, null)

        if (json.isNullOrEmpty()) return emptyList()

        return try {
            val type = object : TypeToken<List<Transaction>>() {}.type
            Gson().fromJson(json, type)
        } catch (e: JsonSyntaxException) {
            // Handle bad/corrupt JSON
            Log.e("TransactionUtils", "Invalid JSON in SharedPreferences", e)
            prefs.edit { remove(TRANSACTION_KEY) } // Clear corrupted data
            emptyList()
        }
    }

    fun saveAllTransactions(context: Context, transactions: List<Transaction>) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val gson = Gson()
        val json = gson.toJson(transactions)
        prefs.edit { putString(TRANSACTION_KEY, json) }
    }

    fun updateTransaction(context: Context, updatedTransaction: Transaction) {
        val transactions = getTransactions(context).toMutableList()

        // Find the index of the transaction with the same ID
        val index = transactions.indexOfFirst { it.id == updatedTransaction.id }

        if (index != -1) {
            // Replace the existing transaction at that index with the updated one
            transactions[index] = updatedTransaction

            // Save the updated transaction list back to SharedPreferences
            saveAllTransactions(context, transactions)
        }
    }
}
