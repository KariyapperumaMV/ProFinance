package com.example.labexam03

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import java.util.*
import androidx.core.content.ContextCompat

class TransactionAdapter(
    private val context: Context,
    private val transactions: MutableList<Transaction>
) : ArrayAdapter<Transaction>(context, 0, transactions) {

    @SuppressLint("SetTextI18n")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val transaction = transactions[position]
        val view = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.overlay_transaction, parent, false)

        val tvTitle = view.findViewById<TextView>(R.id.tvTitle)
        val tvDetails = view.findViewById<TextView>(R.id.tvDetails)
        val btnView = view.findViewById<Button>(R.id.btnView)
        val btnEdit = view.findViewById<Button>(R.id.btnEdit)
        val btnDelete = view.findViewById<Button>(R.id.btnDelete)

        tvTitle.text = transaction.title
        tvDetails.text = " ${transaction.currency} ${transaction.amount} • ${transaction.date}"

        val container = view.findViewById<LinearLayout>(R.id.itemContainer)

        // Change background color based on transaction type
        when (transaction.type.lowercase()) {
            "expense" -> container.setBackgroundColor(ContextCompat.getColor(context, R.color.transaction_red))
            "income" -> container.setBackgroundColor(ContextCompat.getColor(context, R.color.transaction_blue))
        }


        //view button
        btnView.setOnClickListener {
            val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_view_transaction, null)

            val tvPopupTitle = dialogView.findViewById<TextView>(R.id.tvPopupTitle)
            val tvPopupDetails = dialogView.findViewById<TextView>(R.id.tvPopupDetails)
            val btnClosePopup = dialogView.findViewById<Button>(R.id.btnClosePopup)

            tvPopupTitle.text = transaction.title
            tvPopupDetails.text = """
                    Type: ${transaction.type}
                    Category: ${transaction.category}
                    Amount: ${transaction.currency} ${transaction.amount}
                    Date: ${transaction.date}
                """.trimIndent()

            val alertDialog = android.app.AlertDialog.Builder(context)
                .setView(dialogView)
                .create()

            btnClosePopup.setOnClickListener {
                alertDialog.dismiss()
            }

            alertDialog.show()
        }

        //edit button - error occurring
        btnEdit.setOnClickListener {
            val intent = Intent(context, EditTransactionActivity::class.java)
            intent.putExtra("transaction", transaction) // assuming transactionModel is a TransactionModel
            context.startActivity(intent)

        }


        //delete button
        btnDelete.setOnClickListener {
            // Remove from SharedPreferences
            val updatedList = transactions.toMutableList()
            updatedList.removeAt(position)
            TransactionUtils.saveAllTransactions(context, updatedList)

            // Update the adapter data
            transactions.removeAt(position)
            notifyDataSetChanged()

            Toast.makeText(context, "Deleted ${transaction.title}", Toast.LENGTH_SHORT).show()
        }


        return view
    }
}
