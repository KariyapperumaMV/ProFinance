package com.example.labexam03

import android.app.AlertDialog
import android.os.Bundle
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import android.content.ContentValues
import android.os.Build
import android.provider.MediaStore
import android.widget.Button
import android.widget.Toast
import androidx.annotation.RequiresApi


class TransactionHistory : AppCompatActivity() {

    private lateinit var listViewTransactions: ListView

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_transaction_history)

        listViewTransactions = findViewById(R.id.listViewTransactions)
        val btnExport = findViewById<Button>(R.id.btnExport)

        loadTransactions()

        btnExport.setOnClickListener {
            exportTransactionsToDownloads()
        }
    }

    override fun onResume() {
        super.onResume()
        loadTransactions() // Reload the list whenever this screen becomes active
    }

    private fun loadTransactions() {
        try {
            val transactions = TransactionUtils.getTransactions(this)

            if (transactions.isEmpty()) {
                showAlert("No Transactions", "There are no saved transactions to display.")
                return
            }

            val adapter = TransactionAdapter(this, transactions.toMutableList())
            listViewTransactions.adapter = adapter

        } catch (e: Exception) {
            e.printStackTrace()
            showAlert("Error", "Failed to load transaction history:\n${e.message}")
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun exportTransactionsToDownloads() {
        val transactions = TransactionUtils.getTransactions(this)
        if (transactions.isEmpty()) {
            Toast.makeText(this, "No transactions to export.", Toast.LENGTH_SHORT).show()
            return
        }

        val content = transactions.joinToString("\n\n") { txn ->
            """
        ID: ${txn.id}
        Title: ${txn.title}
        Amount: ${txn.amount}
        Date: ${txn.date}
        """.trimIndent()
        }

        val fileName = "transactions_export_${System.currentTimeMillis()}.txt"

        val resolver = contentResolver
        val contentValues = ContentValues().apply {
            put(MediaStore.Downloads.DISPLAY_NAME, fileName)
            put(MediaStore.Downloads.MIME_TYPE, "text/plain")
            put(MediaStore.Downloads.IS_PENDING, 1)
        }

        val collection = MediaStore.Downloads.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
        val itemUri = resolver.insert(collection, contentValues)

        itemUri?.let { uri ->
            resolver.openOutputStream(uri)?.use { outputStream ->
                outputStream.write(content.toByteArray())
                outputStream.flush()
            }

            // Mark as not pending
            contentValues.clear()
            contentValues.put(MediaStore.Downloads.IS_PENDING, 0)
            resolver.update(uri, contentValues, null, null)

            // Show confirmation dialog
            AlertDialog.Builder(this)
                .setTitle("Export Successful")
                .setMessage("Your transactions were saved to the Downloads folder as \"$fileName\".")
                .setPositiveButton("OK", null)
                .show()

        } ?: run {
            Toast.makeText(this, "Failed to create file.", Toast.LENGTH_LONG).show()
        }
    }

    private fun showAlert(title: String, message: String) {
        AlertDialog.Builder(this)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("OK", null)
            .show()
    }
}
