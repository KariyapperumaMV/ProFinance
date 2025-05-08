package com.example.labexam03

data class SummaryItem(
    val category: String,
    val totalAmount: Double,
    val transactionCount: Int,
    val budgetAmount: Double? = null,
    val isOverBudget: Boolean? = null
)
