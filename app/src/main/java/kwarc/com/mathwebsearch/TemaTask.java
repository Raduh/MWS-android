package kwarc.com.mathwebsearch;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.AsyncTask;
import android.view.View;
import android.webkit.WebView;
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
public class TemaTask extends AsyncTask<String, Void, String>{
    private static final String TEMA_URL =
            "http://212.201.44.161/arxiv-ntcir/php/tema_proxy.php";
    HttpClient httpClient = new DefaultHttpClient();

    Activity activity;
    TextView status;
    ProgressBar progr;
    WebView webview;

    final String FROM = "0";
    final String SIZE = "3";

    String jsHeader = null;

    public TemaTask(Activity activity) {
        this.activity = activity;
        status = (TextView) activity.findViewById(R.id.latexmltxt);
        progr = (ProgressBar) activity.findViewById(R.id.latexmlprogr);
        webview = (WebView) activity.findViewById(R.id.results_webvw);
    }

    @Override
    protected void onPreExecute() {
        progr.setVisibility(View.VISIBLE);
        webview.setVisibility(View.GONE);
        status.append("Processing TemaQuery...\n");
    }

    @Override
    protected String doInBackground(String... params) {
        if (params == null || params.length != 2) return null;

        String text = params[0];
        String contentML = params[1];

        List<NameValuePair> getParam = new ArrayList<NameValuePair>(4);
        if (!text.equalsIgnoreCase("")) {
            getParam.add(new BasicNameValuePair("text", text));
        }
        getParam.add(new BasicNameValuePair("math", contentML));
        getParam.add(new BasicNameValuePair("from", FROM));
        getParam.add(new BasicNameValuePair("size", SIZE));

        String paramString = URLEncodedUtils.format(getParam, "utf-8");
        String queryUrl = TEMA_URL + "?" + paramString;
        HttpGet httpGet = new HttpGet(queryUrl);

        try {
            HttpResponse temaHttpResp = httpClient.execute(httpGet);
            String temaResp_str = Util.getStringResponse(temaHttpResp);

            if (jsHeader == null) jsHeader = extractHeader();

            return Util.processTemaResponse(temaResp_str);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onPostExecute(String result) {
        progr.setVisibility(View.GONE);
        status.append("Completed Tema Call");

        webview.getSettings().setJavaScriptEnabled(true);
        webview.getSettings().setBuiltInZoomControls(true);

        webview.setVisibility(View.VISIBLE);
        String htmlContent = "";
        if (jsHeader != null) {
            htmlContent += jsHeader;
        }
        htmlContent += result;

        final String base_url = "http://local/";
        webview.loadDataWithBaseURL(base_url, htmlContent, "text/html", "utf-8", null);

        final String renderedMathJs =
                "javascript:MathJax.Hub.Queue(['Typeset',MathJax.Hub]);";
        webview.loadUrl(renderedMathJs);
    }

    protected String extractHeader() {
        try {
            return Util.getFileContents(activity, R.raw.configmathjax);
        } catch (IOException e) {
            return null;
        }
    }
}
