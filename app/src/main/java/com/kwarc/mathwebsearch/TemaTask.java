package com.kwarc.mathwebsearch;

import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Radu Hambasan
 * @date 4 Oct 2014
 */
public class TemaTask extends AsyncTask<String, Void, String> {
    private static boolean firstCall = true;
    private static boolean isExecuting = false;
    private static boolean hasMoreResults = true;
    private int nextFROM;
    private int hitChunkSIZE;
    private String queryCMML;
    private String queryText;

    private static final String TEMA_URL =
            "http://212.201.44.161/arxiv-ntcir/php/tema_proxy.php";
    HttpClient httpClient = new DefaultHttpClient();

    Activity activity;
    TextView statusDescr;
    ImageView statusColor;
    ProgressBar progr;
    RefreshableWebView webview;

    static ColorDrawable red = new ColorDrawable(0xffff0000);
    static ColorDrawable yellow = new ColorDrawable(0xffffff00);
    static ColorDrawable green = new ColorDrawable(0xff00ff00);

    public static void initialize() {
        firstCall = true;
        isExecuting = false;
        hasMoreResults = true;
    }

    public TemaTask(Activity activity) {
        this.activity = activity;
        statusDescr = (TextView) activity.findViewById(R.id.statusDescr);
        statusColor = (ImageView) activity.findViewById(R.id.statusColor);
        progr = (ProgressBar) activity.findViewById(R.id.progrCircle);
        webview = (RefreshableWebView) activity.findViewById(R.id.results_webvw);
    }

    @Override
    protected void onPreExecute() {
        isExecuting = true;
        progr.setVisibility(View.VISIBLE);
        if (firstCall) webview.setVisibility(View.GONE);
        statusDescr.setVisibility(View.VISIBLE);
        statusColor.setVisibility(View.VISIBLE);
        statusColor.setBackground(yellow);
        statusDescr.setText("Processing CMML Query...");
    }

    @Override
    protected String doInBackground(String... params) {
        if (params == null || params.length != 4) return null;

        String qText = params[0];
        queryText = qText;
        String qContentML = params[1];
        queryCMML = qContentML;
        String qFrom = params[2];
        String qSize = params[3];

        final int expectedHits = Integer.parseInt(qSize);
        hitChunkSIZE = expectedHits;
        nextFROM = Integer.parseInt(qFrom) + expectedHits;

        List<NameValuePair> getParam = new ArrayList<NameValuePair>(4);
        if (!qText.equalsIgnoreCase("")) {
            getParam.add(new BasicNameValuePair("text", qText));
        }
        getParam.add(new BasicNameValuePair("math", qContentML));
        getParam.add(new BasicNameValuePair("from", qFrom));
        getParam.add(new BasicNameValuePair("size", qSize));

        String paramString = URLEncodedUtils.format(getParam, "utf-8");
        String queryUrl = TEMA_URL + "?" + paramString;
        HttpGet httpGet = new HttpGet(queryUrl);

        try {
            HttpResponse temaHttpResp = httpClient.execute(httpGet);
            String temaResp_str = Util.getStringResponse(temaHttpResp);

            ArrayList<String> procHits = Util.processTemaResponse(temaResp_str);

            if (procHits.size() < expectedHits) hasMoreResults = false;
            if (procHits.size() == 0) return "";

            return Util.mergeHits(procHits);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(String result) {
        isExecuting = false;
        final boolean wasFirstCall = firstCall;
        firstCall = false;
        if (result == null) {
            statusDescr.setText("An error occurred.");
            statusColor.setBackground(red);
            return;

        }
        progr.setVisibility(View.GONE);
        statusDescr.setText("Completed TeMa Query");
        statusColor.setBackground(green);
        webview.setVisibility(View.VISIBLE);

        if (result.isEmpty() && wasFirstCall) {
            final String NO_RESULTS_MESSAGE = "<h3>No results.</h3>";
            final String noResultsJs =
                    "javascript:document.getElementById('body').innerHTML='" +
                            NO_RESULTS_MESSAGE + "';";
            webview.loadUrl(noResultsJs);
            hideStatusAfterDelay();
            return;
        }

        if (result.isEmpty()) {
            hideStatusAfterDelay();
            return;
        }

        final String insertHitJs =
                "javascript:" +
                        "div = document.createElement('div');" +
                        "div.innerHTML = '" + result + "';" +
                        "body = document.getElementById('body');" +
                        "body.appendChild(div);";
        webview.loadUrl(insertHitJs);


        final String renderedMathJs =
                "javascript:MathJax.Hub.Queue(['Typeset',MathJax.Hub]);";

        webview.loadUrl(renderedMathJs);

        hideStatusAfterDelay();
        if (hasMoreResults) enableWebViewRefreshing();
        else disableWebViewRefreshing();
    }

    private void hideStatusAfterDelay() {
        final int DELAY = 2000;  // ms

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                statusColor.setVisibility(View.GONE);
                statusDescr.setVisibility(View.GONE);
            }
        }, DELAY);
    }

    private void enableWebViewRefreshing() {
        webview.setOnOverScrollListener(new RefreshableWebView.OnOverScrollListener() {
            @Override
            public void onOverScroll() {
                if (TemaTask.isExecuting) return;
                new TemaTask(activity).execute(queryText, queryCMML,
                        String.valueOf(nextFROM), String.valueOf(hitChunkSIZE));
            }
        });
    }

    private void disableWebViewRefreshing() {
        webview.setOnOverScrollListener(null);
    }
}
