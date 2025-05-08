package com.example.labexam03

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import java.text.DecimalFormat

class SummaryAdapter(
    private val context: Context,
    private val items: List<SummaryItem>
) : BaseAdapter() {

    override fun getCount(): Int = items.size
    override fun getItem(position: Int): Any = items[position]
    override fun getItemId(position: Int): Long = position.toLong()

    private val inflater = LayoutInflater.from(context)
    private val currencyFormat = DecimalFormat("#,##0.00")

    @SuppressLint("SetTextI18n")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = convertView ?: inflater.inflate(R.layout.item_summery, parent, false)
        val item = items[position]

        val tvCategory = view.findViewById<TextView>(R.id.tvCategory)
        val tvAmount = view.findViewById<TextView>(R.id.tvAmount)
        val tvCount = view.findViewById<TextView>(R.id.tvCount)
        val tvBudget = view.findViewById<TextView>(R.id.tvBudget)
        val tvStatus = view.findViewById<TextView>(R.id.tvStatus)

        tvCategory.text = item.category
        tvAmount.text = "Total: ${currencyFormat.format(item.totalAmount)}"
        tvCount.text = "Transactions: ${item.transactionCount}"

        if (item.budgetAmount != null) {
            tvBudget.text = "Budget: ${currencyFormat.format(item.budgetAmount)}"
            val status = if (item.isOverBudget == true) "Over Budget" else "Under Budget"
            tvStatus.text = "Status: $status"
        } else {
            tvBudget.text = "Budget: N/A"
            tvStatus.text = "Status: Not Set"
        }

        return view
    }
}
