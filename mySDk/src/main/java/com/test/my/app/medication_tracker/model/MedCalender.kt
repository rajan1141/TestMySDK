package com.test.my.app.medication_tracker.model

data class MedCalender(
    var Year: String = "",
    var Month: String = "",
    var MonthOfYear: String = "",
    var DayOfWeek: String = "",
    var DayOfMonth: String = "",
    var Date: String = "",
    var IsToday: Boolean = false,
    var IsClickable: Boolean = true
)