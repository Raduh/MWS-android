package kwarc.com.mathwebsearch;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import kwarc.com.mathwebsearch.R;

public class ResultsActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);

        Intent creator = getIntent();
        String qText = creator.getStringExtra(MainActivity.EXTRA_TEXT);
        String qLatex = creator.getStringExtra(MainActivity.EXTRA_LATEX);

        new LatexMLTask(this).execute(qText, qLatex);
    }
}
