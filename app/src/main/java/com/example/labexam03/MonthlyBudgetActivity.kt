package com.example.labexam03
//noinspection SuspiciousImport
import android.R
import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.labexam03.databinding.ActivityMonthlyBudgetBinding

class MonthlyBudgetActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMonthlyBudgetBinding
    private lateinit var budgetAdapter: BudgetAdapter

    @SuppressLint("SimpleDateFormat")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMonthlyBudgetBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupCategorySpinner()
        //setupMonthSpinner()
        setupListView()

        // Prefill amount when either category spinner
        binding.spinnerCategory.setOnItemSelectedListener { _, _, _, _ -> prefillAmount() }

        //binding.spinnerMonth.setOnItemSelectedListener { _, _, _, _ -> prefillAmount() }

        binding.btnSetLimit.setOnClickListener {
            val category = binding.spinnerCategory.selectedItem?.toString()
            val amount = binding.etAmount.text.toString().trim()
            //val month = binding.spinnerMonth.selectedItem?.toString()
            //taking the current month
            val calendar = java.util.Calendar.getInstance()
            val monthFormat = java.text.SimpleDateFormat("MMMM") // "MMMM" gives full month name
            val month = monthFormat.format(calendar.time)

            //current year
            val year = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR)

            if (amount.isBlank()) {
                Toast.makeText(this, "Please enter an amount", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val budget = Budget(
                category = category ?: "",
                month = month,
                year = year,
                amount = amount
            )

            BudgetUtils.saveBudget(this, budget)

            AlertDialog.Builder(this)
                .setTitle("Budget Set")
                .setMessage("Budget of $amount set for $category in $month $year.")
            .setPositiveButton("OK") { _, _ ->
                    binding.etAmount.text.clear()
                    loadBudgets()
                }
                .show()
        }
    }

    private fun setupCategorySpinner() {
        val categories = TransactionUtils.getTransactions(this)
            .map { it.category }
            .toSet()
            .toList()

        val adapter = ArrayAdapter(this, R.layout.simple_spinner_item, categories)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerCategory.adapter = adapter
    }

    /*month spinner
    private fun setupMonthSpinner() {
        val months = listOf(
            "January", "February", "March", "April", "May", "June",
            "July", "August", "September", "October", "November", "December"
        )

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, months)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerMonth.adapter = adapter
    }*/

    private fun setupListView() {
        budgetAdapter = BudgetAdapter(this, mutableListOf()) { budgetToDelete ->
            BudgetUtils.deleteBudget(this, budgetToDelete)
            loadBudgets()
        }

        binding.listViewBudgets.adapter = budgetAdapter
        loadBudgets()
    }

    private fun loadBudgets() {
        val budgets = BudgetUtils.getBudgets(this)
        budgetAdapter.updateData(budgets)
    }

    @SuppressLint("SimpleDateFormat")
    private fun prefillAmount() {
        val selectedCategory = binding.spinnerCategory.selectedItem?.toString()

        val calendar = java.util.Calendar.getInstance()
        val monthFormat = java.text.SimpleDateFormat("MMMM") // "MMMM" gives full month name
        val currentMonth = monthFormat.format(calendar.time)

        val currentYear = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR)

        //fill the amount
        if (!selectedCategory.isNullOrBlank() ) {
            val existingBudget = BudgetUtils.getBudgets(this)
                .find { it.category == selectedCategory && it.month == currentMonth && it.year == currentYear }

            binding.etAmount.setText(existingBudget?.amount ?: "")
        }
    }

    // Spinner extension
    private fun Spinner.setOnItemSelectedListener(onChange: (parent: Spinner, pos: Int, id: Long, view: View?) -> Unit) {
        this.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                onChange(this@setOnItemSelectedListener, position, id, view)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }
}
