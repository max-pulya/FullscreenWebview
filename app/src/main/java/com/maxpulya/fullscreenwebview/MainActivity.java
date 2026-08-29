package com.maxpulya.fullscreenwebview;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import android.text.Editable;
import android.view.InflateException;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.webkit.CookieManager;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;


import com.google.android.material.button.MaterialButtonToggleGroup;

import java.util.zip.Inflater;

public class MainActivity extends AppCompatActivity {
    WebView webView=null;
    AlertDialog dialog=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        hideSystemBars();
        webView = new WebView(this);
        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setDatabaseEnabled(true);

        webSettings.setSaveFormData(true);
        webSettings.setSupportZoom(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setDisplayZoomControls(false);
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);

        webSettings.setMediaPlaybackRequiresUserGesture(false);
        webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);

        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptThirdPartyCookies(webView, true);


        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                // Возвращаем false, чтобы WebView сам загружал URL
                return false;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                // Можно выполнить действия после загрузки
            }
        });
        webView.loadUrl("https://max-pulya.github.io/startpage");
        setContentView(webView);

    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getPointerCount()==3){
            if (dialog!=null && dialog.isShowing())return super.dispatchTouchEvent(event);
            if (dialog!=null){
                dialog.dismiss();
                dialog=null;
            }

            AlertDialog.Builder builder= new AlertDialog.Builder(this);
            LayoutInflater inflater= LayoutInflater.from(this);
            View dialogView= inflater.inflate(R.layout.popup_window,null);
            EditText loadUrlField=dialogView.findViewById(R.id.loadUrlField);
            loadUrlField.setText(webView.getUrl());
            Button reloadBtn=dialogView.findViewById(R.id.reloadButton);
            Button newTabBtn=dialogView.findViewById(R.id.newTabButton);
            builder.setView(dialogView);
            dialog=builder.create();
            dialog.show();

            loadUrlField.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_DONE) {
                        webView.loadUrl(v.getText().toString());
                        dialog.dismiss();
                        dialog=null;
                        return true;
                    }
                    return false;
                }
            });
            reloadBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    webView.reload();
                }
            });
            newTabBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(MainActivity.this, MainActivity.class);
                    startActivity(intent);
                }
            });
        }
        return super.dispatchTouchEvent(event);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            hideSystemBars();
        }
    }
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        webView.invalidate();
    }
    private void hideSystemBars() {
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        WindowInsetsControllerCompat controller = new WindowInsetsControllerCompat(getWindow(), getWindow().getDecorView());
        if (controller != null) {
            controller.hide(WindowInsetsCompat.Type.systemBars());
            controller.setSystemBarsBehavior(WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
        }
    }
    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) webView.goBack();
         else super.onBackPressed();
    }
}