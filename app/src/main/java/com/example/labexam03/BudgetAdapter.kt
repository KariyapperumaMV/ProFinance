package com.example.labexam03

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*

class BudgetAdapter(
    private val context: Context,
    private var budgets: MutableList<Budget>,
    private val onDelete: (Budget) -> Unit
) : BaseAdapter() {

    override fun getCount(): Int = budgets.size

    override fun getItem(position: Int): Any = budgets[position]

    override fun getItemId(position: Int): Long = position.toLong()

    fun updateData(newBudgets: List<Budget>) {
        budgets = newBudgets.toMutableList()
        notifyDataSetChanged()
    }

    @SuppressLint("SetTextI18n")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.item_budget, parent, false)

        val budget = budgets[position]

        val tvBudgetInfo = view.findViewById<TextView>(R.id.tvBudgetInfo)
        val btnDelete = view.findViewById<ImageButton>(R.id.btnDelete)

        tvBudgetInfo.text = "${budget.category} - ${budget.month} ${budget.year}: ${budget.amount}"


        btnDelete.setOnClickListener {
            AlertDialog.Builder(context)
                .setTitle("Delete Budget")
                .setMessage("Delete budget for ${budget.category} in ${budget.month} ${budget.year}?")
                .setPositiveButton("Yes") { _, _ ->
                    onDelete(budget)
                }
                .setNegativeButton("No", null)
                .show()
        }

        return view
    }
}
