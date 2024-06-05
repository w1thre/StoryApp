package com.codewithre.storyapp

import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.codewithre.storyapp.view.login.LoginActivity
import com.codewithre.storyapp.view.main.MainActivity
import com.codewithre.storyapp.view.utils.EspressoIdlingResource
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LoginLogoutTest {
    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    @Before
    fun setup() {
        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)
    }

    @After
    fun shutdown() {
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource)
    }

    @Test
    fun loginAndLogoutTest() {
        // Launch the activity
        ActivityScenario.launch(LoginActivity::class.java)

        // Perform login
        onView(withId(R.id.ed_login_email)).perform(typeText("gigi@mail.com"), closeSoftKeyboard())
        onView(withId(R.id.ed_login_password)).perform(typeText("password"), closeSoftKeyboard())
        onView(withId(R.id.loginButton)).perform(click())
        onView(withText(R.string.yeah)).check(matches(isDisplayed()))
        onView(withText(R.string.continue_text)).perform(click())


        // Verify that the main activity is displayed after login
        onView(withId(R.id.main)).check(matches(isDisplayed()))

        openActionBarOverflowOrOptionsMenu(getApplicationContext())

        // Perform logout
        onView(withText(R.string.logout)).perform(click())

        // Verify that the login activity is displayed after logout
        onView(withId(R.id.welcome)).check(matches(isDisplayed()))
    }

}