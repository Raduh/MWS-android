package kwarc.com.mathwebsearch;

import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Handler;
import android.view.View;
import android.webkit.WebView;
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
    private static final String TEMA_URL =
            "http://212.201.44.161/arxiv-ntcir/php/tema_proxy.php";
    HttpClient httpClient = new DefaultHttpClient();

    Activity activity;
    TextView statusDescr;
    ImageView statusColor;
    ProgressBar progr;
    WebView webview;

    final String FROM = "0";
    final String SIZE = "5";

    ColorDrawable red = new ColorDrawable(0xffff0000);
    ColorDrawable yellow = new ColorDrawable(0xffffff00);
    ColorDrawable green = new ColorDrawable(0xff00ff00);

    public TemaTask(Activity activity) {
        this.activity = activity;
        statusDescr = (TextView) activity.findViewById(R.id.statusDescr);
        statusColor = (ImageView) activity.findViewById(R.id.statusColor);
        progr = (ProgressBar) activity.findViewById(R.id.progrCircle);
        webview = (WebView) activity.findViewById(R.id.results_webvw);
    }

    @Override
    protected void onPreExecute() {
        progr.setVisibility(View.VISIBLE);
        webview.setVisibility(View.GONE);
        statusDescr.setText("Processing CMML Query...");
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

            return Util.processTemaResponse(temaResp_str);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(String result) {
        if (result == null) {
            statusDescr.setText("An error occurred.");
            statusColor.setBackground(red);
            return;

        }
        progr.setVisibility(View.GONE);
        statusDescr.setText("Completed TeMa Query");
        statusColor.setBackground(green);

        webview.setVisibility(View.VISIBLE);
        String htmlContent = "";
        htmlContent += result;
        // should we escape math elements?

        final String insertHitJs =
                "javascript:document.getElementById('body').innerHTML='" + htmlContent +"';";
        webview.loadUrl(insertHitJs);


        final String renderedMathJs =
                "javascript:MathJax.Hub.Queue(['Typeset',MathJax.Hub]);";

        webview.loadUrl(renderedMathJs);

        hideStatusAfterDelay();
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
}
