package com.example.productivityapp.models

enum class Role(val code: String) {
    DOCTOR("Doctor"),
    PATIENT("Patient");

    companion object {
        fun byCode(code: String?): Role? {
            if (code == null) return null
            return values().firstOrNull { it.code == code }
                ?: throw IllegalArgumentException("Unknown role: $code")
        }
    }
}