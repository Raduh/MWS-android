package kwarc.com.mathwebsearch;

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
            "http://212.201.44.161/arxiv-ntcir/php/latexml_proxy.php";
    HttpClient httpClient = new DefaultHttpClient();
    HttpPost httpPost = new HttpPost(LATEXML_URL);

    TextView latexMlTxt;
    ProgressBar latexMlProgr;

    Activity activity;

    String qText;
    String qCML;

    public LatexMLTask(Activity activity) {
        this.activity = activity;
        latexMlTxt = (TextView) activity.findViewById(R.id.latexmltxt);
        latexMlProgr = (ProgressBar) activity.findViewById(R.id.latexmlprogr);
    }

    @Override
    protected void onPreExecute() {
        latexMlTxt.setText("Processing...");
        latexMlProgr.setVisibility(View.VISIBLE);
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
        latexMlProgr.setVisibility(View.GONE);
        if (result != null) {
            latexMlTxt.setText("latexMl conversion done\n");
            qCML = Util.getContentMathML(result);
            if (qCML == null) {
                latexMlTxt.append("Could not find CML\n");
                return;
            }
            activity = null;
            new TemaTask(activity).execute(qText, qCML);
        } else {
            latexMlTxt.setText("You were fucked");
        }
    }


}
