package com.synq.app.presentation.screens.chatdetail

import org.junit.Assert.assertEquals
import org.junit.Test
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class DateUtilsTest {

    @Test
    fun formatHeaderDate_whenToday_returnsToday() {
        val todayTimestamp = System.currentTimeMillis()
        val result = formatHeaderDate(todayTimestamp)
        assertEquals("Today", result)
    }

    @Test
    fun formatHeaderDate_whenYesterday_returnsYesterday() {
        val yesterdayCal = Calendar.getInstance().apply {
            add(Calendar.DAY_OF_YEAR, -1)
        }
        val result = formatHeaderDate(yesterdayCal.timeInMillis)
        assertEquals("Yesterday", result)
    }

    @Test
    fun formatHeaderDate_whenOlderDate_returnsFormattedDate() {
        val olderDateCal = Calendar.getInstance().apply {
            set(2023, Calendar.JANUARY, 15) // Fixed date: Jan 15, 2023
        }

        // Ensure it's not today or yesterday
        val diffDays = (System.currentTimeMillis() - olderDateCal.timeInMillis) / (1000 * 60 * 60 * 24)
        if (diffDays <= 1) {
            olderDateCal.add(Calendar.DAY_OF_YEAR, -5)
        }

        val result = formatHeaderDate(olderDateCal.timeInMillis)

        val expectedFormat = SimpleDateFormat("MMMM d, yyyy", Locale.getDefault())
        val expectedDate = expectedFormat.format(Date(olderDateCal.timeInMillis))

        assertEquals(expectedDate, result)
    }
}
