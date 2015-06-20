package us.albertwang.metrics;

import android.content.Intent;
import android.content.Context;
import android.database.sqlite.*;
import android.database.Cursor;
import android.graphics.Color;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TableLayout;
import android.widget.TextView;
import android.view.View;
import android.util.Log;

import java.util.ArrayList;
import java.io.File;
import java.util.Iterator;


public class TodoList extends ActionBarActivity {

    public int ADD_METRIC_REQ_CODE = 1;
    ArrayList<MetricEntry> metricEntryArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todo_list);
        TableLayout tl = (TableLayout) findViewById(R.id.table_layout);

    }

    public void addAMetricToTableLayout(View v) {
        Intent addActivityIntent = new Intent(this, AddMetricActivity.class);
        startActivityForResult(addActivityIntent, ADD_METRIC_REQ_CODE);
    }

    @Override
    public void onStart() {
        super.onStart();
        MetricItemDBHelper mDbHelper = new MetricItemDBHelper(this); // Creates database
        SQLiteDatabase dbReader = mDbHelper.getReadableDatabase();
        String query = "SELECT * from " + MetricItemDB.MetricItemEntry.TABLE_NAME;
        // Populate the TableLayout TODO list
        // Define a projection that specifies which columns from the database
        // you will actually use after this query.

        String sortOrder = MetricItemDB.MetricItemEntry.COLUMN_NAME_ID + " DESC";
        Cursor c = dbReader.rawQuery(query, null);

        // Okay, we should something to our list.
        TableLayout taskTableLayout = (TableLayout) findViewById(R.id.table_layout);
        TableLayout duedateTableLayout = (TableLayout) findViewById(R.id.day_due_table_layout);
        metricEntryArrayList = new ArrayList();

        // Start filling in the TodoList
        for (int x=0; x<c.getCount(); x++) {
            MetricEntry metricEntry = new MetricEntry(
                    c.getString(1), //ID
                    c.getString(2), // TODO
                    c.getInt(3), //COMMENT
                    c.getInt(4), //DUEDATE
                    c.getInt(5),
                    c.getInt(6));
            // TODO start filling in the TodoList
            TextView newTask = new TextView(getApplicationContext());
            newTask.setText(metricEntry.todo);
            newTask.setTextColor(Color.BLUE);
            taskTableLayout.addView(newTask);

            // Get due_date
            TextView newDueDate = new TextView(getApplicationContext());
            newDueDate.setText((CharSequence) metricEntry.due_date);
            newDueDate.setTextColor(Color.BLUE);
            duedateTableLayout.addView(newDueDate);

            Log.i("Metrics", "Added todo:" + metricEntry.todo);
            Log.i("Metrics", "Added due_date:" + metricEntry.due_date);
            metricEntryArrayList.add(metricEntry);

            c.moveToNext();
        }

        mDbHelper.close();
    }

    @Override
    public void onPause() {
        super.onPause();
        MetricItemDBHelper mDbHelper = new MetricItemDBHelper(this);
        SQLiteDatabase dbWriter = mDbHelper.getWritableDatabase();

        Iterator metricEntryIter = metricEntryArrayList.iterator();
        dbWriter.execSQL("DROP TABLE IF EXISTS " + );
        while (metricEntryIter.hasNext()) {
            dbWriter.execSQL("")
        }

        mDbHelper.close();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            // Okay, we should something to our list.
            TableLayout tl = (TableLayout) findViewById(R.id.table_layout);
            Bundle bundle = (Bundle) data.getExtras();
            TextView tv_temp = new TextView(getApplicationContext());
            tv_temp.setText(bundle.getString("task"));
            tv_temp.setTextColor(Color.BLUE);
            tl.addView(tv_temp);

            // Get due_date
            TableLayout duedate_tl = (TableLayout) findViewById(R.id.day_due_table_layout);
            tv_temp = new TextView(getApplicationContext());
            tv_temp.setText(bundle.getString("duedate"));
            tv_temp.setTextColor(Color.BLUE);
            duedate_tl.addView(tv_temp);

        } else {
            TableLayout tl = (TableLayout) findViewById(R.id.table_layout);
            TextView tv_temp = new TextView(getApplicationContext());
            tv_temp.setText("Added a metric 2");
            tl.addView(tv_temp);
        }
    }

    /**
     * Interface for the Controller to handle SQLite transactions
     */
    public class MetricItemDBHelper extends SQLiteOpenHelper {

        public static final String DATABASE_NAME = "MetricItem.db";
        public static final int DATABASE_VERSION = 1;

        public MetricItemDBHelper (Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        public void onCreate(SQLiteDatabase db) {
            db.execSQL(MetricItemDB.SQL_CREATE_ENTRIES);
        }

        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // This database is only a cache for online data, so its upgrade policy is
            // to simply to discard the data and start over
            db.execSQL(MetricItemDB.SQL_DELETE_ENTRIES);
            onCreate(db);
        }
        public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            onUpgrade(db, oldVersion, newVersion);
        }
    }

    /**
     * SQL Database handling for creating tables
     * Shall only handled by MetricItemDBHelper
     */
    public final class MetricItemDB {

        private static final String TEXT_TYPE = " TEXT";
        private static final String INT_TYPE = " INTEGER PRIMARY KEY";
        private static final String COMMA_SEP = ",";
        private static final String SQL_CREATE_ENTRIES =
                "CREATE TABLE " + MetricItemEntry.TABLE_NAME + " (" +
                        MetricItemEntry.COLUMN_NAME_ID + INT_TYPE + COMMA_SEP+
                        MetricItemEntry.COLUMN_NAME_TODO + TEXT_TYPE + COMMA_SEP +
                        MetricItemEntry.COLUMN_NAME_COMMENT + TEXT_TYPE + COMMA_SEP +
                        MetricItemEntry.COLUMN_NAME_DUE_DATE + INT_TYPE + COMMA_SEP +
                        MetricItemEntry.COLUMN_NAME_COMPLETED_TIME + INT_TYPE + COMMA_SEP +
                        MetricItemEntry.COLUMN_NAME_COMPLETED + TEXT_TYPE +
                " )";

        private static final String SQL_DELETE_ENTRIES =
                "DROP TABLE IF EXISTS " + MetricItemEntry.TABLE_NAME;

        public void MetricItem(){}

        /* Inner class that defines table contents */
        /* This is a fucking waste of time. I can duplicate MetricEntry */
        public abstract class MetricItemEntry implements BaseColumns {
            public static final String TABLE_NAME = "entry";                // Table name
            public static final String COLUMN_NAME_ID = "id"; // Processed as an integer
            public static final String COLUMN_NAME_TODO = "todo"; // String that shouldn't be multi-line
            public static final String COLUMN_NAME_COMMENT = "comment"; // Standard multi-line String
            public static final String COLUMN_NAME_DUE_DATE = "due_date"; // handled like a calendar date
            public static final String COLUMN_NAME_COMPLETED_TIME = "completed_date"; // handled like a calendar date/time
            public static final String COLUMN_NAME_COMPLETED = "completed"; // handled like a boolean
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_todo_list, menu);
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
