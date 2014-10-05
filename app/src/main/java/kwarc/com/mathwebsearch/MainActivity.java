package kwarc.com.mathwebsearch;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

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
        findViewById(R.id.fillinImg).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fillInExample();
            }
        });
    }

    public void startSearch(View view) {
        String text = textInput.getText().toString();
        String latex = latexInput.getText().toString();
        if (latex.equalsIgnoreCase("")) {
            final String message = "The latex query cannot be empty.\n" +
                    "If the formula can match anything, use a qvar: ?x";
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            return;
        }

        Intent searchIntent = new Intent(this, ResultsActivity.class);
        searchIntent.putExtra(EXTRA_TEXT, text);
        searchIntent.putExtra(EXTRA_LATEX, latex);
        startActivity(searchIntent);
    }

    private void fillInExample() {
        final String textExample = "Fermat";
        final String latexExample = "a^?n + b^?n=c^?n";

        textInput.setText(textExample);
        latexInput.setText(latexExample);
    }

}
