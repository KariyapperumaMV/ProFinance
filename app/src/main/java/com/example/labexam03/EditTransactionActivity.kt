package com.example.labexam03

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Build
import android.os.Bundle
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity

class EditTransactionActivity : AppCompatActivity() {

    private lateinit var etTitle: EditText
    private lateinit var etCategory: EditText
    private lateinit var spinnerType: Spinner
    private lateinit var spinnerCurrency: Spinner
    private lateinit var etAmount: EditText
    private lateinit var etDate: EditText
    private lateinit var btnUpdate: Button

    private var transaction: Transaction? = null
    private lateinit var typeAdapter: ArrayAdapter<String>
    private lateinit var currencyAdapter: ArrayAdapter<String>

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_transaction_edit)

        // Initialize UI components
        etTitle = findViewById(R.id.etTitle)
        etCategory = findViewById(R.id.etCategory)
        spinnerType = findViewById(R.id.spinnerTransactionType)
        spinnerCurrency = findViewById(R.id.spinnerCurrency)
        etAmount = findViewById(R.id.etAmount)
        etDate = findViewById(R.id.etDate)
        btnUpdate = findViewById(R.id.btnUpdate)

        // Setup adapters
        val types = listOf("Income", "Expense")
        val currencies = listOf("USD", "LKR", "Pound", "AED")

        typeAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, types)
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerType.adapter = typeAdapter

        currencyAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, currencies)
        currencyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerCurrency.adapter = currencyAdapter

        // Get transaction from intent
        transaction = intent.getSerializableExtra("transaction", Transaction::class.java)

        if (transaction == null) {
            AlertDialog.Builder(this)
                .setTitle("Error")
                .setMessage("Failed to load transaction for editing.")
                .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
                .show()
            return
        }



        transaction?.let {
            etTitle.setText(it.title)
            etCategory.setText(it.category)
            spinnerType.setSelection(types.indexOf(it.type))
            spinnerCurrency.setSelection(currencies.indexOf(it.currency))
            etAmount.setText(it.amount)
            etDate.setText(it.date)
        }

        btnUpdate.text = "Update"

        btnUpdate.setOnClickListener {
            val title = etTitle.text.toString().trim()
            val category = etCategory.text.toString().trim()
            val type = spinnerType.selectedItem.toString()
            val currency = spinnerCurrency.selectedItem.toString()
            val amount = etAmount.text.toString().trim()
            val date = etDate.text.toString().trim()

            if (title.isEmpty() || amount.isEmpty() || date.isEmpty()) {
                showToast("Please fill all required fields.")
                return@setOnClickListener
            }

            val amountValue = amount.toDoubleOrNull()
            if (amountValue == null) {
                showToast("Invalid amount entered.")
                return@setOnClickListener
            }

            AlertDialog.Builder(this)
                .setTitle("Confirm Update")
                .setMessage("Are you sure you want to update this transaction?")
                .setPositiveButton("Yes") { dialog, _ ->
                    val updatedTransaction = Transaction(
                        id = transaction?.id ?: "",
                        title = title,
                        category = category,
                        type = type,
                        currency = currency,
                        amount = amount,
                        date = date
                    )

                    TransactionUtils.updateTransaction(this, updatedTransaction)
                    showToast("Transaction updated successfully!")
                    dialog.dismiss()
                    finish()
                }
                .setNegativeButton("No") { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
