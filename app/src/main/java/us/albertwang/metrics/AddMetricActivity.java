package us.albertwang.metrics;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentTransaction;
import android.app.TimePickerDialog;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.content.Intent;
import android.widget.TimePicker;

import java.util.Calendar;

public class AddMetricActivity extends Activity {

    public int year, month, day, hour, minute;
    public static String dueDate = "";
    public static String dueTime = "";
    public static String TAG = "Metrix";
    public int estimatedCompletionTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_metric);

        // MetricEntry.SuperSimpleDate.getDueDatePrettyFormat();

        Bundle b = getIntent().getExtras();
        if (b != null && b.containsKey("todo")) {
            ((EditText) findViewById(R.id.editText)).setText(b.getString("todo"));
            ((EditText) findViewById(R.id.editText3)).setText(b.getString("comment"));

            Log.i("albewang", "" + b.getInt("duedate"));
            dueTime = b.getString("dueTime");
            ((Button) findViewById(R.id.dueTimePicker)).setText(dueTime);

            dueDate = b.getString("dueDate");
            ((Button) findViewById(R.id.dueDatePicker)).setText(dueDate);

            estimatedCompletionTime = b.getInt("estimatedCompletionTime");
        }

        View decorView = getWindow().getDecorView();
        // Hide the status bar.
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
        // Remember that you should never show the action bar if the
        // status bar is hidden, so hide that too if necessary.
        ActionBar actionBar = getActionBar();
        actionBar.hide();
    }

    public void saveSettings(View v) {
        EditText taskEditText = (EditText) findViewById(R.id.editText);
        EditText commentsEditText = (EditText) findViewById(R.id.editText3);
        Bundle bundle = new Bundle();
        bundle.putString("task", taskEditText.getText().toString());
        bundle.putString("dueDate", dueDate);
        bundle.putString("dueTime", dueTime);
        bundle.putInt("laborType", MetricEntry.CREATIVE_LABOR);
        bundle.putString("comments", commentsEditText.getText().toString());
        Intent outIntent = new Intent();
        outIntent.putExtras(bundle);
        setResult(RESULT_OK, outIntent);
        finish();
    }

    public void showDueDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment();
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        newFragment.show(ft, "dueDatePickerDialog");
    }


    public void showTimePickerDialog(View v) {
        DialogFragment newFragment = new TimePickerFragment();
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        newFragment.show(ft, "timePickerDialog");
    }

    public interface DatePickerListener {
        public void datePicked(DatePicker view, int year, int month, int day);
    }

    @SuppressLint("ValidFragment")
    public class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            dueDate = Integer.toString(year) + "/";
            if (month < 10) {
                dueDate = dueDate + "0" + month + "/";
            } else {
                dueDate = dueDate + month + "/";
            }

            if (day < 10) {
                dueDate = dueDate + "0" + day;
            } else {
                dueDate = dueDate + day;
            }
            Button thisButton = (Button) findViewById(R.id.dueDatePicker);
            thisButton.setText(dueDate);
        }
    }

    public interface TimePickerListener {
        public void timePicked(View view, int hourOfDay, int minute);
    }

    public class TimePickerFragment extends DialogFragment
            implements TimePickerDialog.OnTimeSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current time as the default values for the picker
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            // Create a new instance of TimePickerDialog and return it
            return new TimePickerDialog(getActivity(), this, hour, minute,
                    DateFormat.is24HourFormat(getActivity()));
        }

        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            if (hourOfDay < 10) {
                dueTime = "0" + hourOfDay;
            } else {
                dueTime = "" + hourOfDay;
            }

            if (minute < 10) {
                dueTime = dueTime + ":0" + minute;
            } else {
                dueTime = dueTime + ":" + minute;
            }
            Button thisButton = (Button) findViewById(R.id.dueTimePicker);
            thisButton.setText(dueTime);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action
        // bar if it is present.
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
