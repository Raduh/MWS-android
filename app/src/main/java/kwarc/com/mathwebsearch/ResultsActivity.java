package kwarc.com.mathwebsearch;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;

import kwarc.com.mathwebsearch.R;

public class ResultsActivity extends Activity {

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);

        Intent creator = getIntent();
        String qText = creator.getStringExtra(MainActivity.EXTRA_TEXT);
        String qLatex = creator.getStringExtra(MainActivity.EXTRA_LATEX);

        WebView webview = (WebView) findViewById(R.id.results_webvw);
        webview.getSettings().setJavaScriptEnabled(true);
        webview.getSettings().setBuiltInZoomControls(true);

        final String TEMPLATE_PATH =
                "file:///android_asset/html/index.html";
        webview.loadUrl(TEMPLATE_PATH);

        new LatexMLTask(this).execute(qText, qLatex);
    }
}
