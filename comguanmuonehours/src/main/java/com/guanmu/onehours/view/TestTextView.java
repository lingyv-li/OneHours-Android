package com.guanmu.onehours.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import androidx.cardview.widget.CardView;
import android.view.MotionEvent;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.guanmu.onehours.model.MPoint;


/**
 * Created by bartl on 4/18/2016.
 * <p/>
 * A TextView for testing text point(TYPE_NORMAL)
 */
public class TestTextView extends WebView implements Testable {
    private MPoint point;
    private boolean isLoaded = false;

    @SuppressLint("SetJavaScriptEnabled")
    public TestTextView(Context context) {
        super(context);
        getSettings().setJavaScriptEnabled(true);
        getSettings().setDomStorageEnabled(true);
        getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        setBackgroundColor(Color.TRANSPARENT);

        String html = "<style>.invisible {display: none}</style><script type='text/javascript' src='file:///android_asset/js/MathJax/MathJax.js?config=TeX-AMS_HTML'></script><script type='text/x-mathjax-config'>MathJax.Hub.Config({showProcessingMessages: false, messageStyle: 'none'});</script><span id='math'></span>";
        loadDataWithBaseURL(null, html, "text/html", "utf-8", "");

        setWebViewClient(new WebViewClient() {
            public void onPageFinished(WebView view, String url) {
                loadFormula();
                isLoaded = true;
            }
        });

        addJavascriptInterface(this, "MyApp");
    }

    @JavascriptInterface
    public void resize(final float height) {
        post(new Runnable() {
            @Override
            public void run() {
                setLayoutParams(new CardView.LayoutParams(getResources().getDisplayMetrics().widthPixels, (int) (height * getResources().getDisplayMetrics().density)));
            }
        });
    }

    private String doubleEscapeTeX(String s) {
        s = s.replace("\\", "\\\\");
        s = s.replace("'", "\\\'");
        s = s.replace("\n", "");
        return s;
    }

    // Disable touch event
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return false;
    }

    private void loadText(String text){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            evaluateJavascript("javascript:document.getElementById('math').innerHTML='"
                    + doubleEscapeTeX(text) + "';", null);
        } else {
            loadUrl("javascript:document.getElementById('math').innerHTML='" + doubleEscapeTeX(text) + "';");
        }
    }

    private void loadFormula() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            evaluateJavascript("javascript:MathJax.Hub.Queue(['Typeset',MathJax.Hub]);", null);
            evaluateJavascript("javascript:MyApp.resize(document.body.getBoundingClientRect().height+10)", null);
        } else {
            loadUrl("javascript:MathJax.Hub.Queue(['Typeset',MathJax.Hub]);");
            loadUrl("javascript:MyApp.resize(document.body.getBoundingClientRect().height+10)");
        }
    }

    public void setData(final MPoint point) {
        this.point = point;

        final String text = point.getContent()
                .replace("<b>", "<small><small><font color=\"#FF0000\">(</font></small></small><font color=\"#FFFFFF\">")
                .replace("</b>", "</font><small><small><font color=\"#FF0000\">)</font></small></small>");

        if (!isLoaded) {
            setWebViewClient(new WebViewClient() {
                public void onPageFinished(WebView view, String url) {
                    loadText(text);
                    loadFormula();
                    isLoaded = true;
                }
            });
        } else {
            loadText(text);
            loadFormula();
        }
    }

    public void showAll() {
        String text = point.getContent().replace("<b>", "<small><small><font color=\"#FF0000\">(</font></small></small>")
                .replace("</b>", "<small><small><font color=\"#FF0000\">)</font></small></small>")
                .replaceAll("</?p .*?>", "").replace("</br>", "");
        loadText(text);
        loadFormula();
    }
}