package com.example.app_statistics.model

enum class TimeRange(val label: String, val days: Int? = null) {
    TODAY("Сегодня", 1),
    LAST_7_DAYS("7 дней", 7),
    LAST_30_DAYS("30 дней", 30),
    CUSTOM("Выбранные даты")
}
