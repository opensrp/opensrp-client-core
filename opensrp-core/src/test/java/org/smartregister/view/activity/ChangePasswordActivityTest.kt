package org.smartregister.view.activity

import android.content.Intent
import android.view.KeyEvent
import android.view.MenuItem
import android.webkit.WebView
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.rules.ActivityScenarioRule
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.robolectric.RobolectricTestRunner
import org.smartregister.BaseUnitTest
import org.smartregister.account.AccountHelper

/**
 * Created by ndegwamartin on 11/03/2022.
 */

@RunWith(RobolectricTestRunner::class)
class ChangePasswordActivityTest : BaseUnitTest() {

    val intent =
        Intent(ApplicationProvider.getApplicationContext(), ChangePasswordActivity::class.java)
            .putExtra(
                AccountHelper.CONFIGURATION_CONSTANTS.ISSUER_ENDPOINT_URL,
                "https://keycloak.test.smartregister.org/auth/realms/test-realm/"
            )

    @get:Rule
    val rule: ActivityScenarioRule<ChangePasswordActivity> =
        ActivityScenarioRule<ChangePasswordActivity>(intent);

    @Test
    fun testOnCreateInitializesCorrectly() {

        val scenario = rule.scenario
        scenario.onActivity { activity ->
            Assert.assertNotNull(activity)
            Assert.assertNotNull(activity.intent)

            val url: String? =
                activity.intent.getStringExtra(AccountHelper.CONFIGURATION_CONSTANTS.ISSUER_ENDPOINT_URL)
            Assert.assertNotNull(url)
            Assert.assertEquals(
                "https://keycloak.test.smartregister.org/auth/realms/test-realm/",
                url
            )
        }
    }

    @Test
    fun testOnKeyDownInvokesWebviewGoBackOnKeyDown() {

        val scenario = rule.scenario
        scenario.onActivity { activity ->

            Assert.assertNotNull(activity.webView)

            val webView: WebView? = Mockito.spy(activity.webView)
            Assert.assertNotNull(webView)

            val keyEvent: KeyEvent = Mockito.mock(KeyEvent::class.java)

            Mockito.doNothing().`when`(webView)?.goBack()

            activity.onKeyDown(KeyEvent.KEYCODE_BACK, keyEvent)

            Mockito.verify(webView, Mockito.atMostOnce())?.goBack()
        }
    }

    @Test
    fun testOnOptionsItemSelectedNavigatesBackOnUpButtonClick() {
        val menuItem: MenuItem = Mockito.mock(MenuItem::class.java)
        Mockito.doReturn(android.R.id.home).`when`(menuItem).itemId

        val scenario = rule.scenario
        scenario.onActivity { activity ->

            Assert.assertNotNull(activity)

            Assert.assertFalse(activity.isFinishing)

            activity.onOptionsItemSelected(menuItem)

            Assert.assertTrue(activity.isFinishing)
        }
    }
}