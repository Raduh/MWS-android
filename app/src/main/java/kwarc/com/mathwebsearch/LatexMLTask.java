package kwarc.com.mathwebsearch;

import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ImageView;
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

    TextView statusDescr;
    ImageView statusColor;
    ProgressBar progrCircle;

    Activity activity;

    String qText;
    String qCML;

    ColorDrawable red = new ColorDrawable(0xffff0000);
    ColorDrawable yellow = new ColorDrawable(0xffffff00);
    ColorDrawable green = new ColorDrawable(0xff00ff00);

    public LatexMLTask(Activity activity) {
        this.activity = activity;
        statusDescr = (TextView) activity.findViewById(R.id.statusDescr);
        statusColor = (ImageView) activity.findViewById(R.id.statusColor);
        progrCircle = (ProgressBar) activity.findViewById(R.id.progrCircle);
    }

    @Override
    protected void onPreExecute() {
        statusDescr.setText("Converting to Latex to MathML...");
        statusColor.setBackground(yellow);
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
                statusColor.setBackground(red);
                return;
            }

            new TemaTask(activity).execute(qText, qCML);
        } else {
            statusDescr.setText("An error occurred.");
            statusColor.setBackground(red);
        }
    }
}
