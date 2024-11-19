package com.privateid.auth

import android.content.pm.PackageManager
import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebChromeClient
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    private var webview: WebView? = null

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        permissions.forEach { (permission, isGranted) ->
            if (isGranted) {
                Toast.makeText(applicationContext, "$permission granted", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(applicationContext, "$permission denied", Toast.LENGTH_SHORT).show()
            }
        }
        if (permissions.all { it.value }) {
            openUrl()
        } else {
            Toast.makeText(applicationContext, "Permissions denied", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        // Adjust safe area for root view
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { view, insets ->
            val systemBarsInsets = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(systemBarsInsets.left, systemBarsInsets.top, systemBarsInsets.right, systemBarsInsets.bottom)
            insets
        }

        webview = findViewById(R.id.webview)
        setupWebView()

        checkPermissions()

    }

    private fun setupWebView () {
        webview?.apply {
            settings.javaScriptEnabled = true
            settings.domStorageEnabled = true
            settings.loadWithOverviewMode = true
            settings.useWideViewPort = true
            webChromeClient = object : WebChromeClient() {
                override fun onPermissionRequest(request: android.webkit.PermissionRequest) {
                    request.grant(request.resources) // Automatically grant permissions
                }
            }
            webViewClient = object : WebViewClient() {
                override fun onReceivedError(
                    view: WebView, errorCode: Int, description: String, failingUrl: String
                ) {
                    Toast.makeText(applicationContext, "WebView Error: $description", Toast.LENGTH_LONG).show()
                }
            }
//            loadUrl("https://xcm.cvsauth.com/")
        }
    }

    private fun checkPermissions() {
        val permissionsNeeded = mutableListOf<String>()
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            permissionsNeeded.add(android.Manifest.permission.CAMERA)
        }
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            permissionsNeeded.add(android.Manifest.permission.RECORD_AUDIO)
        }

        if (permissionsNeeded.isNotEmpty()) {
            requestPermissionLauncher.launch(permissionsNeeded.toTypedArray())
        } else {
            openUrl()
        }
    }


    private fun openUrl() {
        webview?.loadUrl("https://xcm.cvsauth.com/")
    }
}