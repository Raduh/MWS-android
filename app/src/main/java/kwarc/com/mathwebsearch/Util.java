package kwarc.com.mathwebsearch;

import android.content.Context;

import org.apache.http.HttpResponse;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Radu Hambasan
 * @date 04 Oct 2014
 */
public class Util {
    public static final String TITLE_PREFIX = "arxiv.org: ";

    public static String getStringResponse(HttpResponse response) throws IOException {
        InputStream inStr = response.getEntity().getContent();
        BufferedReader buf = new BufferedReader(new InputStreamReader(inStr));

        StringBuilder xmlContent = new StringBuilder();
        String line;
        while ((line = buf.readLine()) != null) {
            xmlContent.append(line);
        }
        buf.close();

        return xmlContent.toString();
    }

    public static String getContentMathML(String latexMLResponse) {
        String CML_REGEX = "<annotation-xml.*MWS-Query\">(.*)<.annotation-xml>";
        Pattern pattern = Pattern.compile(CML_REGEX, Pattern.DOTALL);

        try {
            JSONObject latexMLJSON = new JSONObject(latexMLResponse);
            String math = latexMLJSON.getString("result");

            Matcher matcher = pattern.matcher(math);
            if (matcher.find()) {
                return matcher.group(1);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getFileContents(Context context, int rawId) throws IOException {
        InputStream raw = context.getResources().openRawResource(rawId);
        BufferedReader buf = new BufferedReader(new InputStreamReader(raw));

        StringBuilder content = new StringBuilder();
        String line;
        while ((line = buf.readLine()) != null) {
            content.append(line);
        }
        buf.close();

        return content.toString();

    }

    private static String fillSnippet(String snippet, JSONArray maths) throws JSONException{
        String result = "" + snippet;

        for (int i = 0; i < maths.length(); i++) {
            JSONObject math = maths.getJSONObject(i);
            String source = math.getString("source");
            String replace = math.getString("replaces");

            result = result.replaceAll(replace, source);
        }

        return result;
    }

    private static String processHit(JSONObject hit) throws JSONException {
        JSONArray snippets = hit.getJSONArray("snippets");
        JSONArray maths = hit.getJSONArray("maths");
        JSONObject metadata = hit.getJSONObject("metadata");
        final String title = TITLE_PREFIX + metadata.getString("title");

        StringBuilder snippetBuilder = new StringBuilder();
        snippetBuilder.append("<h2>").append(title).append("</h2>");
        for (int i = 0; i < snippets.length(); i++) {
            String curr = snippets.getString(i);
            snippetBuilder.append(curr);
        }
        String mergedSnip = snippetBuilder.toString();
        return fillSnippet(mergedSnip, maths);
    }

    // TODO: return ArrayList<String> with the hits
    public static String processTemaResponse(String temaResponse) throws JSONException {
        JSONObject temaRespJSON = new JSONObject(temaResponse);
        JSONArray hits = temaRespJSON.getJSONArray("hits");

        ArrayList<String> processedHits = new ArrayList<String>();
        for (int i = 0; i < hits.length(); i++) {
            JSONObject curr = hits.getJSONObject(i);
            processedHits.add(processHit(curr));
        }

        StringBuilder htmlBuilder = new StringBuilder();
        for (String elem : processedHits) {
            htmlBuilder.append(elem);
            htmlBuilder.append("<hr/>");
        }

        return htmlBuilder.toString();
    }
}
