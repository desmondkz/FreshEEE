package com.ntu.fresheee;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class WebViewHeathDeclarationActivity extends AppCompatActivity {

    private WebView healthDeclarationwebView;
    private String url = "https://sso.wis.ntu.edu.sg/webexe88/owa/sso_login1.asp?t=2&p2=https://wis.ntu.edu.sg/pls/webexe88/declare_overseas.plans&extra=&pg=";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view_heath_declaration);

        healthDeclarationwebView = (WebView) findViewById(R.id.health_declaration_webview);
        healthDeclarationwebView.setWebViewClient(new WebViewClient());
        healthDeclarationwebView.getSettings().setJavaScriptEnabled(true);
        healthDeclarationwebView.loadUrl(url);
    }

    @Override
    public void onBackPressed() {
        if(healthDeclarationwebView.canGoBack()) {
            healthDeclarationwebView.goBack();
        }
        else {
            super.onBackPressed();
        }
    }
}