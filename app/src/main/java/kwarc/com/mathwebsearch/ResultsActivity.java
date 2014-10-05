package kwarc.com.mathwebsearch;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;

public class ResultsActivity extends Activity {

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);

        findViewById(R.id.statusDescr).setVisibility(View.VISIBLE);
        findViewById(R.id.statusColor).setVisibility(View.VISIBLE);

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
