package com.example.labexam03

import android.graphics.Color
import android.os.Bundle
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter

class SummaryActivity : AppCompatActivity() {

    private lateinit var pieChartExpenses: PieChart
    private lateinit var pieChartIncome: PieChart
    private lateinit var listViewSummary: ListView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_summary)

        pieChartExpenses = findViewById(R.id.pieChartExpenses)
        pieChartIncome = findViewById(R.id.pieChartIncome)
        listViewSummary = findViewById(R.id.listViewSummary)

        val transactions = TransactionUtils.getTransactions(this)
        val budgets = BudgetUtils.getBudgets(this)

        val expenseSummary = mutableMapOf<String, MutableList<Transaction>>()
        val incomeSummary = mutableMapOf<String, MutableList<Transaction>>()

        // Separate by type and category
        transactions.forEach {
            val map = if (it.type == "Expense") expenseSummary else incomeSummary
            map.getOrPut(it.category) { mutableListOf() }.add(it)
        }

        val expenseItems = expenseSummary.map { (category, txns) ->
            val total = txns.sumOf { it.amount.toDoubleOrNull() ?: 0.0 }
            val count = txns.size

            //budget of the category
            val calendar = java.util.Calendar.getInstance()
            val currentMonth = calendar.get(java.util.Calendar.MONTH) // 0-based
            val currentYear = calendar.get(java.util.Calendar.YEAR)
            val currentMonthName = listOf(
                "January", "February", "March", "April", "May", "June",
                "July", "August", "September", "October", "November", "December"
            )[currentMonth]

            val budget = budgets.find {
                it.category.equals(category, ignoreCase = true) &&
                        it.month.equals(currentMonthName, ignoreCase = true) &&
                        it.year == currentYear
            }

            SummaryItem(
                category = category,
                totalAmount = total,
                transactionCount = count,
                budgetAmount = budget?.amount?.toDoubleOrNull(),
                isOverBudget = budget?.amount?.toDoubleOrNull()?.let { total > it }
            )
        }.sortedByDescending { it.totalAmount }

        val incomeItems = incomeSummary.map { (category, txns) ->
            SummaryItem(
                category = category,
                totalAmount = txns.sumOf { it.amount.toDoubleOrNull() ?: 0.0 },
                transactionCount = txns.size,
                budgetAmount = null,
                isOverBudget = null
            )
        }.sortedByDescending { it.totalAmount }

        showPieChart(pieChartExpenses, expenseItems, "Expenses")
        showPieChart(pieChartIncome, incomeItems, "Income")

        val adapter = SummaryAdapter(this, expenseItems + incomeItems)
        listViewSummary.adapter = adapter
    }

    private fun showPieChart(chart: PieChart, data: List<SummaryItem>, label: String) {
        val entries = data.map { PieEntry(it.totalAmount.toFloat(), it.category) }
        val dataSet = PieDataSet(entries, label)
        dataSet.colors = listOf(
            Color.rgb(128, 0, 128), // red
            Color.rgb(148, 0, 211), // blue
            Color.rgb(147, 112, 219), // green
            Color.rgb(230, 230, 250) // amber
        )
        dataSet.sliceSpace = 1f
        dataSet.valueTextSize = 12f
        dataSet.valueFormatter = PercentFormatter(chart)
        dataSet.valueTextColor = Color.WHITE

        val pieData = PieData(dataSet)
        chart.data = pieData
        chart.description.isEnabled = false
        chart.setUsePercentValues(true)
        chart.setEntryLabelColor(Color.WHITE)
        chart.legend.textColor= Color.WHITE
        chart.legend.orientation = Legend.LegendOrientation.HORIZONTAL
        chart.legend.isWordWrapEnabled = true
        chart.invalidate()
    }

    // Helper to extract month
    private fun Transaction.dateMonthFormatted(): String {
        return this.date.split("-").getOrNull(1)?.let {
            val monthIndex = it.toIntOrNull()?.minus(1) ?: return ""
            listOf(
                "January", "February", "March", "April", "May", "June",
                "July", "August", "September", "October", "November", "December"
            ).getOrNull(monthIndex) ?: ""
        } ?: ""
    }
}
