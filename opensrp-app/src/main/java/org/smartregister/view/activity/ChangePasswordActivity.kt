package org.smartregister.view.activity

import android.graphics.Bitmap
import android.os.Bundle
import android.view.KeyEvent
import android.view.MenuItem
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import org.apache.commons.lang3.StringUtils
import org.smartregister.R
import org.smartregister.account.AccountHelper
import org.smartregister.view.activity.ChangePasswordActivity.*

/**
 * Created by ndegwamartin on 08/03/2022.
 *
 * This activity gets launched when client core detects that your user account requires a password reset action
 */
class ChangePasswordActivity : AppCompatActivity() {
    var webView: WebView? = null
        private set
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_password)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val issuerEndpointURL: String? = if (savedInstanceState == null) {
            val extras = intent.extras
            extras?.getString(AccountHelper.CONFIGURATION_CONSTANTS.ISSUER_ENDPOINT_URL)
        } else {
            savedInstanceState.getSerializable(AccountHelper.CONFIGURATION_CONSTANTS.ISSUER_ENDPOINT_URL) as String?
        }

        progressBar = findViewById<View>(R.id.changePasswordProgressBar) as ProgressBar

        webView = findViewById<View>(R.id.changePasswordWV) as WebView

        //To resolve to URL of format https://keycloak.<env>.smartregister.org/auth/realms/<realm-name>/account
        webView!!.loadUrl(issuerEndpointURL + AccountHelper.OAUTH.PASSWORD_RESET_ENDPOINT)
        webView?.apply {
            settings.apply {
                javaScriptEnabled = true
                loadsImagesAutomatically = true
            }
            webViewClient = OpenSRPWebViewClient()
            webChromeClient = OpenSRPWebChromeClient()
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK && this.webView!!.canGoBack()) {
            this.webView?.goBack()
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

    private inner class OpenSRPWebViewClient : WebViewClient() {

        override fun onPageStarted(view: WebView, url: String, favicon: Bitmap?) {
            super.onPageStarted(view, url, favicon)
            view.visibility = View.GONE
            progressBar.visibility = View.VISIBLE
        }

        override fun onPageFinished(view: WebView, url: String) {
            super.onPageFinished(view, url)
            val visible = View.VISIBLE
            view.visibility = visible
            progressBar.visibility = View.GONE
        }
    }

    private inner class OpenSRPWebChromeClient : WebChromeClient() {

        override fun onReceivedTitle(view: WebView?, title: String?) {
            super.onReceivedTitle(view, title)
            if (StringUtils.isNotBlank(title)) {
                this@ChangePasswordActivity.title = title
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}