package com.example.labexam03

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import java.util.*

class AddTransactionActivity : AppCompatActivity() {

    private lateinit var etTitle: EditText
    private lateinit var etCategory: EditText
    private lateinit var etAmount: EditText
    private lateinit var etDate: EditText
    private lateinit var btnSubmit: Button
    private lateinit var spinnerCurrency: Spinner
    private lateinit var spinnerTransactionType: Spinner
    private lateinit var sharedPreferences: SharedPreferences

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_transaction)

        // Initialize UI components
        etTitle = findViewById(R.id.etTitle)
        etCategory = findViewById(R.id.etCategory)
        etAmount = findViewById(R.id.etAmount)
        etDate = findViewById(R.id.etDate)
        btnSubmit = findViewById(R.id.btnSubmit)
        spinnerCurrency = findViewById(R.id.spinnerCurrency)
        spinnerTransactionType = findViewById(R.id.spinnerTransactionType)

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("TransactionPrefs", Context.MODE_PRIVATE)

        // Set up currency spinner
        val currencies = arrayOf("USD", "LKR", "Pound", "AED")
        val currencyAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, currencies)
        spinnerCurrency.adapter = currencyAdapter

        // Restore last selected currency
        val savedCurrency = sharedPreferences.getString("currency_type", null)
        savedCurrency?.let {
            val index = currencies.indexOf(it)
            if (index >= 0) spinnerCurrency.setSelection(index)
        }

        // Set up transaction type spinner
        val types = arrayOf("Income", "Expense")
        val typeAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, types)
        spinnerTransactionType.adapter = typeAdapter

        // Set up date picker
        etDate.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
                val formattedDate = String.format("%02d/%02d/%04d", selectedDay, selectedMonth + 1, selectedYear)
                etDate.setText(formattedDate)
            }, year, month, day)

            datePickerDialog.show()
        }

        // Handle submit button click
        btnSubmit.setOnClickListener {
            val title = etTitle.text.toString().trim()
            val category = etCategory.text.toString().trim()
            val amount = etAmount.text.toString().trim()
            val date = etDate.text.toString().trim()
            val currency = spinnerCurrency.selectedItem.toString()
            val type = spinnerTransactionType.selectedItem.toString()

            // Basic validation
            if (title.isEmpty() || amount.isEmpty() || date.isEmpty()) {
                showAlert("Validation Error", "Please fill all required fields.")
                return@setOnClickListener
            }

            val amountValue = amount.toDoubleOrNull()
            if (amountValue == null) {
                showAlert("Validation Error", "Please enter a valid numeric amount.")
                return@setOnClickListener
            }

            // Save currency preference
            sharedPreferences.edit().putString("currency_type", currency).apply()

            val transaction = Transaction(
                UUID.randomUUID().toString(),
                title,
                category,
                type,
                currency,
                amount,
                date
            )

            try {
                TransactionUtils.saveTransaction(this, transaction)
                showAlert("Success", "Transaction added successfully!") {
                    finish() // Go back to previous screen
                }
            } catch (e: Exception) {
                e.printStackTrace()
                showAlert("Error", "Failed to save transaction:\n${e.message}")
            }
        }
    }

    // Function to show alert dialogs
    private fun showAlert(title: String, message: String, onOk: (() -> Unit)? = null) {
        AlertDialog.Builder(this)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
                onOk?.invoke()
            }
            .show()
    }
}
