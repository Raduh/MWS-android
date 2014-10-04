package kwarc.com.mathwebsearch;

import org.apache.http.HttpResponse;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Radu Hambasan
 * @date 04 Oct 2014
 */
public class Util {
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

    public static String processTemaResponse(String temaResponse) throws JSONException {
        JSONObject temaRespJSON = new JSONObject(temaResponse);
        int total = temaRespJSON.getInt("total");

        return ("Retrieved " + total + " results.\n");
    }
}
