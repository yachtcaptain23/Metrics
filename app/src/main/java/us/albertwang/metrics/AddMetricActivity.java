package us.albertwang.metrics;

import android.app.PendingIntent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.content.Intent;

public class AddMetricActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        setContentView(R.layout.activity_add_metric);
    }

    public void saveSettings(View v) {
        EditText taskEditText = (EditText) findViewById(R.id.editText);
        EditText dateDueEditText = (EditText) findViewById(R.id.editText2);
        EditText commentsEditText = (EditText) findViewById(R.id.editText3);
        Bundle bundle = new Bundle();
        bundle.putString("task", taskEditText.getText().toString());
        bundle.putString("duedate", dateDueEditText.getText().toString());
        bundle.putString("comments", commentsEditText.getText().toString());
        Intent outIntent = new Intent();
        outIntent.putExtras(bundle);
        setResult(RESULT_OK, outIntent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_metric, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
