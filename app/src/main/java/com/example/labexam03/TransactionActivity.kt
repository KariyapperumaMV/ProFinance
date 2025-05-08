package com.example.labexam03

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.labexam03.databinding.ActivityTransactionBinding

class TransactionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTransactionBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTransactionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnAddNew.setOnClickListener {
            // Navigate to Add Transaction Page
            startActivity(Intent(this, AddTransactionActivity::class.java))
        }

        binding.btnTransactionHistory.setOnClickListener {
            // Navigate to Transaction History Page
            startActivity(Intent(this, TransactionHistory::class.java))
        }
    }
}