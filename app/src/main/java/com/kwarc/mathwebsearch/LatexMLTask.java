package com.kwarc.mathwebsearch;

import android.app.Activity;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Radu Hambasan
 * @date 04 Oct 2014
 */
public class LatexMLTask extends AsyncTask<String, Void, String> {
    private static final String LATEXML_URL =
            "http://jupiter.eecs.jacobs-university.de/arxiv-ntcir/php/latexml_proxy.php";
    HttpClient httpClient = new DefaultHttpClient();
    HttpPost httpPost = new HttpPost(LATEXML_URL);

    TextView statusDescr;
    ProgressBar progrCircle;

    Activity activity;

    String qText;
    String qCML;

    static int red = 0xffff0000;
    static int yellow = 0xffffff00;

    public LatexMLTask(Activity activity) {
        this.activity = activity;
        statusDescr = (TextView) activity.findViewById(R.id.statusDescr);
        progrCircle = (ProgressBar) activity.findViewById(R.id.progrCircle);
    }

    @Override
    protected void onPreExecute() {
        statusDescr.setText("Converting to Latex to MathML...");
        statusDescr.setTextColor(yellow);
        statusDescr.setVisibility(View.VISIBLE);
        progrCircle.setVisibility(View.VISIBLE);
    }

    @Override
    protected String doInBackground(String... params) {
        if (params == null || params.length != 2) return null;

        qText = params[0];
        String latex = params[1];

        List<NameValuePair> postParam = new ArrayList<NameValuePair>(2);
        //noinspection SpellCheckingInspection
        postParam.add(new BasicNameValuePair("profile", "mwsq"));
        postParam.add(new BasicNameValuePair("tex", latex));

        try {
            httpPost.setEntity(new UrlEncodedFormEntity(postParam));
            HttpResponse response = httpClient.execute(httpPost);

            return Util.getStringResponse(response);
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(String result) {
        progrCircle.setVisibility(View.GONE);
        if (result != null) {
            statusDescr.setText("LatexML conversion done\n");
            qCML = Util.getContentMathML(result);
            if (qCML == null) {
                statusDescr.setText("Could not find CML\n");

                return;
            }

            final String DEFAULT_FROM = "0";
            final String DEFAULT_SIZE = "5";

            TemaTask.initialize();
            new TemaTask(activity).execute(qText, qCML, DEFAULT_FROM, DEFAULT_SIZE);
        } else {
            statusDescr.setText("An error occurred.");
            statusDescr.setTextColor(red);
        }
    }
}
