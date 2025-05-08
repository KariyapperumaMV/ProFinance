package com.example.labexam03

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import androidx.core.content.edit

object BudgetUtils {
    private const val PREFS_NAME = "BudgetPrefs"
    private const val BUDGET_KEY = "budgets"

    fun saveBudget(context: Context, newBudget: Budget) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val gson = Gson()
        val budgets = getBudgets(context).toMutableList()

        // Remove existing budget for same category and month
        budgets.removeAll { it.category == newBudget.category && it.month.equals(newBudget.month, ignoreCase = true) }

        budgets.add(newBudget)
        prefs.edit { putString(BUDGET_KEY, gson.toJson(budgets)) }

        budgets.removeAll {
            it.category == newBudget.category &&
                    it.month.equals(newBudget.month, ignoreCase = true) &&
                    it.year == newBudget.year
        }

    }

    fun getBudgets(context: Context): List<Budget> {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val json = prefs.getString(BUDGET_KEY, null) ?: return emptyList()
        val type = object : TypeToken<List<Budget>>() {}.type
        return try {
            Gson().fromJson(json, type)
        } catch (e: Exception) {
            prefs.edit { remove(BUDGET_KEY) }
            emptyList()
        }
    }

    fun getBudgetFor(context: Context, category: String, month: String, year: Int): Budget? {
        return getBudgets(context).find {
            it.category.equals(category, ignoreCase = true) &&
                    it.month.equals(month, ignoreCase = true) &&
                    it.year == year
        }
    }

    fun deleteBudget(context: Context, budgetToDelete: Budget) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val gson = Gson()
        val budgets = getBudgets(context).toMutableList()

        budgets.removeAll {
            it.category == budgetToDelete.category &&
                    it.month == budgetToDelete.month &&
                    it.year == budgetToDelete.year
        }

        prefs.edit {
            putString(BUDGET_KEY, gson.toJson(budgets))
        }
    }
}
