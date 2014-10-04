package kwarc.com.mathwebsearch;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

/**
 * @author Radu Hambasan
 * @date 04 Oct 2014
 */
public class MainActivity extends Activity {
    public static final String EXTRA_TEXT = "text";
    public static final String EXTRA_LATEX = "latex";

    EditText textInput;
    EditText latexInput;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textInput = (EditText) findViewById(R.id.text_input);
        latexInput = (EditText) findViewById(R.id.latex_input);
    }

    public void startSearch(View view) {
        String text = textInput.getText().toString();
        String latex = latexInput.getText().toString();
        Intent searchIntent = new Intent(this, ResultsActivity.class);
        searchIntent.putExtra(EXTRA_TEXT, text);
        searchIntent.putExtra(EXTRA_LATEX, latex);
        startActivity(searchIntent);
    }
}
