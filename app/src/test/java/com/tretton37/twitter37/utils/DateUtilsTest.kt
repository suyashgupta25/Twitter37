package com.tretton37.twitter37.utils

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test

class DateUtilsTest {

    @Test
    fun testDateWithEmptyString() {
        //when
        val result = DateUtils.formatDateString("")
        //then
        assertEquals("just now",result)
    }

    @Test
    fun testDateWithSpecialCharacter() {
        //when
        val result = DateUtils.formatDateString("!@#$%%%")
        //then
        assertEquals("just now",result)
    }

    @Test
    fun testDateWithCorrectDate() {
        //when
        val result = DateUtils.formatDateString("Wed Jul 22 11:14:34 -0700 2018")
        //then
        assertNotEquals("just now",result)
        assertNotEquals("a minute ago",result)
        assertEquals(true, result.contains("days ago"))
    }
}