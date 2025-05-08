package com.example.labexam03


import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.labexam03.databinding.ActivityHomeBinding

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnTransaction.setOnClickListener {
            startActivity(Intent(this, TransactionActivity::class.java))
        }

        binding.btnSummary.setOnClickListener {
            startActivity(Intent(this, SummaryActivity::class.java))
        }

        binding.btnBudget.setOnClickListener {
            startActivity(Intent(this, MonthlyBudgetActivity::class.java))
        }
    }
}
