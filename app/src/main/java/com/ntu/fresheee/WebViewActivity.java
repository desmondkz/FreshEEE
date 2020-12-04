package com.ntu.fresheee;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.webkit.WebView;
import android.widget.TextView;

public class WebViewActivity extends AppCompatActivity {

    private WebView webView;

    private TextView subjecttv, sendertv, senderEmailtv, dateTimetv;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().setTitle("");

        setContentView(R.layout.activity_web_view);

        subjecttv = (TextView) findViewById(R.id.webview_subject);
        sendertv = (TextView) findViewById(R.id.webview_sender);
        dateTimetv = (TextView) findViewById(R.id.webview_datetime);

        subjecttv.setText(getIntent().getStringExtra("subject"));
        sendertv.setText(getIntent().getStringExtra("sender"));
        dateTimetv.setText(getIntent().getStringExtra("datetime"));

        String data = getIntent().getStringExtra("html");
        webView = (WebView) findViewById(R.id.main_webview);
        webView.getSettings().setDomStorageEnabled(true);
        webView.loadDataWithBaseURL(null, data, "text/html", "utf-8", null);

//        String url = getIntent().getStringExtra("url");
//        webView = (WebView) findViewById(R.id.main_webview);
//        webView.setWebViewClient(new WebViewClient());
//        webView.getSettings().setDomStorageEnabled(true);
//        webView.getSettings().setJavaScriptEnabled(true);
//        webView.loadUrl(url);
    }

    @Override
    public void onBackPressed() {
        if(webView.canGoBack()) {
            webView.goBack();
        }
        else {
            super.onBackPressed();
        }
    }
}