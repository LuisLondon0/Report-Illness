package com.example.report_illness.models

class Patient(
    id: Int,
    names: String,
    lastNames: String,
    contact: String,
    var birthDate: String,
    var gender: String
) : Person(id, names, lastNames, contact)

