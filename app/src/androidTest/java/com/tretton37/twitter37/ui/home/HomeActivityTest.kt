package com.tretton37.twitter37.ui.home

import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions.click
import android.support.test.espresso.action.ViewActions.swipeUp
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.ViewMatchers.isDisplayed
import android.support.test.espresso.matcher.ViewMatchers.withId
import android.support.test.filters.LargeTest
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import com.tretton37.twitter37.R
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@LargeTest
@RunWith(AndroidJUnit4::class)
class HomeActivityTest {

    @Rule
    @JvmField
    val rule = ActivityTestRule<HomeActivity>(HomeActivity::class.java)

    @Test
    fun shouldBeAbleToLoadTweets() {
        onView(withId(R.id.rv_tweets)).check(matches(isDisplayed()))
    }

    @Test
    fun shouldBeAbleToScrollTweets() {
        onView(withId(R.id.rv_tweets)).check(matches(isDisplayed()))
        onView(withId(R.id.rv_tweets)).perform(swipeUp())
        onView(withId(R.id.rv_tweets)).perform(swipeUp())
    }

    @Test
    fun shouldBeAbleToOpenSearchScreen() {
        onView(withId(R.id.rv_tweets)).check(matches(isDisplayed()))
        onView(withId(R.id.action_search)).perform(click())
        onView(withId(R.id.search_layout)).check(matches(isDisplayed()))
    }
}