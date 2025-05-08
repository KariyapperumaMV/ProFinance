package com.example.labexam03

import java.io.Serializable

data class Transaction(
    val id: String,
    var title: String,
    val category: String,
    var type: String,
    val currency: String,
    var amount: String,
    var date: String
) : Serializable

