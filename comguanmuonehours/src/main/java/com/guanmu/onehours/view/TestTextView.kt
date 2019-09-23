package com.guanmu.onehours.view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.view.MotionEvent
import android.view.ViewGroup.LayoutParams
import android.webkit.JavascriptInterface
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import com.guanmu.onehours.model.MPoint


/**
 * Created by bartl on 4/18/2016.
 *
 *
 * A TextView for testing text point(TYPE_NORMAL)
 */
class TestTextView @SuppressLint("SetJavaScriptEnabled")
constructor(context: Context) : WebView(context), Testable {
    private var point: MPoint? = null
    private var isLoaded = false

    init {
        settings.javaScriptEnabled = true
        settings.domStorageEnabled = true
        settings.cacheMode = WebSettings.LOAD_NO_CACHE
        setBackgroundColor(Color.TRANSPARENT)

        val html = "<style>.invisible {display: none}</style><script type='text/javascript' src='file:///android_asset/js/MathJax/MathJax.js?config=TeX-AMS_HTML'></script><script type='text/x-mathjax-config'>MathJax.Hub.Config({showProcessingMessages: false, messageStyle: 'none'});</script><span id='math'></span>"
        loadDataWithBaseURL(null, html, "text/html", "utf-8", "")

        webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView, url: String) {
                loadFormula()
                isLoaded = true
            }
        }

        addJavascriptInterface(this, "MyApp")
    }

    @JavascriptInterface
    fun resize(height: Float) {
        post { layoutParams = LayoutParams(resources.displayMetrics.widthPixels, (height * resources.displayMetrics.density).toInt()) }
    }

    private fun doubleEscapeTeX(s: String): String {
        return s.replace("\\", "\\\\")
                .replace("'", "\\\'")
                .replace("\n", "")
    }

    // Disable touch event
    override fun onTouchEvent(event: MotionEvent): Boolean {
        return false
    }

    private fun loadText(text: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            evaluateJavascript("javascript:document.getElementById('math').innerHTML='"
                    + doubleEscapeTeX(text) + "';", null)
        } else {
            loadUrl("javascript:document.getElementById('math').innerHTML='" + doubleEscapeTeX(text) + "';")
        }
    }

    private fun loadFormula() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            evaluateJavascript("javascript:MathJax.Hub.Queue(['Typeset',MathJax.Hub]);", null)
            evaluateJavascript("javascript:MyApp.resize(document.body.getBoundingClientRect().height+10)", null)
        } else {
            loadUrl("javascript:MathJax.Hub.Queue(['Typeset',MathJax.Hub]);")
            loadUrl("javascript:MyApp.resize(document.body.getBoundingClientRect().height+10)")
        }
    }

    override fun setData(mPoint: MPoint) {
        this.point = mPoint

        val text = mPoint.content!!
                .replace("<b>", "<small><small><font color=\"#FF0000\">(</font></small></small><font color=\"#FFFFFF\">")
                .replace("</b>", "</font><small><small><font color=\"#FF0000\">)</font></small></small>")

        if (!isLoaded) {
            webViewClient = object : WebViewClient() {
                override fun onPageFinished(view: WebView, url: String) {
                    loadText(text)
                    loadFormula()
                    isLoaded = true
                }
            }
        } else {
            loadText(text)
            loadFormula()
        }
    }

    override fun showAll() {
        val text = point!!.content!!.replace("<b>", "<small><small><font color=\"#FF0000\">(</font></small></small>")
                .replace("</b>", "<small><small><font color=\"#FF0000\">)</font></small></small>")
                .replace("</?p .*?>".toRegex(), "").replace("</br>", "")
        loadText(text)
        loadFormula()
    }
}