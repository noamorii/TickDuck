package cz.cvut.fel.pda.tickduck.utils

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter

object CalendarUtils {
    lateinit var selectedDay: LocalDate

    fun daysInMonthArray(date: LocalDate): ArrayList<LocalDate?> {
        val daysInMonthArray: ArrayList<LocalDate?> = ArrayList()
        val daysInMonth: Int = YearMonth.from(date).lengthOfMonth()
        val dayOfWeek = selectedDay.withDayOfMonth(1).dayOfWeek.value
        for (i in 1..42) {
            if (i <= dayOfWeek || i > daysInMonth + dayOfWeek) daysInMonthArray.add(null)
            else daysInMonthArray.add(LocalDate.of(selectedDay.year, selectedDay.month, i - dayOfWeek))
        }
        return daysInMonthArray
    }

    fun monthYearFromDate(date: LocalDate): String? {
        val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("MMMM yyyy")
        return date.format(formatter)
    }


    fun daysInWeekArray(selectedDay: LocalDate): ArrayList<LocalDate?> {
        val days: ArrayList<LocalDate?> = ArrayList()
        var current = sundayForDate(selectedDay)
        val endDate = current?.plusWeeks(1)
        while (current != null && current.isBefore(endDate)) {
            days.add(current)
            current = current.plusDays(1)
        }
        return days
    }

    private fun sundayForDate(current: LocalDate): LocalDate? {
        val weekAgo = current.minusWeeks(1)
        var day = current
        while (day.isAfter(weekAgo)) {
            if (day.dayOfWeek == DayOfWeek.SUNDAY) return day
            day = day.minusDays(1)
        }
        return null
    }
}