package com.example.report_illness.models

open class Disease (
    var id: Int,
    var name: String,
    var description: String,
    var urgency: String,
    var vaccine: Boolean,
    var treatment: Boolean
)