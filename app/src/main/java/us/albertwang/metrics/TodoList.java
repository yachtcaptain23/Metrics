package us.albertwang.metrics;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.content.Context;
import android.database.sqlite.*;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TextView;
import android.view.View;
import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;


public class TodoList extends Activity {

    public int ADD_METRIC_REQUEST_CODE = 1;
    private final int EDIT_REQUEST_CODE = 2;
    ArrayList<MetricEntry> mMetricEntryArrayList;
    ArrayList<MetricEntry> mCompletedMetrics;
    MetricItemDBHelper mDatabaseHelper;
    SQLiteDatabase mDatabase;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;


    public String TAG = "Metrix";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDatabaseHelper = new MetricItemDBHelper(this); // Creates database
        mDatabase = mDatabaseHelper.getWritableDatabase();
        if (mMetricEntryArrayList == null)
            mMetricEntryArrayList = new ArrayList<MetricEntry>();
        if (mCompletedMetrics == null)
            mCompletedMetrics = new ArrayList<MetricEntry>();


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setContentView(R.layout.activity_todo_list);
            mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
            mRecyclerView.setHasFixedSize(true);

            mLayoutManager = new LinearLayoutManager(this);
            mRecyclerView.setLayoutManager(mLayoutManager);

            // specify an adapter (see also next example)
            loadDatabase();
            mAdapter = new MyAdapter();
            mRecyclerView.setAdapter(mAdapter);

        } else {
            setContentView(R.layout.activity_todo_list);
            TableLayout tl = (TableLayout) findViewById(R.id.table_layout);
            // Okay, we should something to our list.
            TableLayout taskTableLayout = (TableLayout) findViewById(R.id.table_layout);
            TableLayout duedateTableLayout = (TableLayout) findViewById(R.id.day_due_table_layout);
            mMetricEntryArrayList = new ArrayList<MetricEntry>();
            loadDatabase(taskTableLayout, duedateTableLayout);
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

    /**
     * Lollipop and newer as it integrates with RecyclerView
     */
    private void loadDatabase() {
        String query = "SELECT * from " + MetricItemDB.MetricItemEntry.TABLE_NAME;

        String sortOrder = MetricItemDB.MetricItemEntry.COLUMN_NAME_ID + " DESC";
        Cursor c = mDatabase.rawQuery(query, null);

        // Start filling in the TodoList
        MetricEntry.SuperSimpleDate ssd = new MetricEntry.SuperSimpleDate();
        Log.e(TAG, "Current count = " + c.getCount());
        c.moveToFirst();

        for (int x=0; x<c.getCount(); x++) {
            MetricEntry metricEntry = new MetricEntry(
                    c.getString(1), //TODO
                    c.getString(2), // Comment
                    new MetricEntry.SuperSimpleDate(c.getInt(3)), // due_date
                    new MetricEntry.SuperSimpleDate(c.getInt(4)), // completed_date
                    c.getInt(5), // isCompleted
                    c.getInt(6)); // duration

            mMetricEntryArrayList.add(metricEntry);
            c.moveToNext();
        }
    }

    private void loadDatabase(TableLayout taskTableLayout, TableLayout duedateTableLayout) {
        String query = "SELECT * from " + MetricItemDB.MetricItemEntry.TABLE_NAME;

        String sortOrder = MetricItemDB.MetricItemEntry.COLUMN_NAME_ID + " DESC";
        Cursor c = mDatabase.rawQuery(query, null);

        // Start filling in the TodoList
        MetricEntry.SuperSimpleDate ssd = new MetricEntry.SuperSimpleDate();
        Log.e(TAG, "Current count = " + c.getCount());
        c.moveToFirst();

        for (int x=0; x<c.getCount(); x++) {
            MetricEntry metricEntry = new MetricEntry(
                    c.getString(1), //TODO
                    c.getString(2), // Comment
                    new MetricEntry.SuperSimpleDate(c.getInt(3)), // due_date
                    new MetricEntry.SuperSimpleDate(c.getInt(4)), // completed_date
                    c.getInt(5), // isCompleted
                    c.getInt(6)); // duration
            TextView newTask = new TextView(getApplicationContext());
            newTask.setText(metricEntry.todo);
            newTask.setTextColor(Color.BLUE);
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP) {
                taskTableLayout.addView(newTask);
            }

            // Get due_date
            TextView newDueDate = new TextView(getApplicationContext());
            newDueDate.setText(Integer.toString(metricEntry.due_date.getCurrentDate()));
            newDueDate.setTextColor(Color.BLUE);
            duedateTableLayout.addView(newDueDate);

            mMetricEntryArrayList.add(metricEntry);

            c.moveToNext();
        }
    }

    public void addAMetricToTableLayout(View v) {
        Intent addActivityIntent = new Intent(this, AddMetricActivity.class);
        startActivityForResult(addActivityIntent, ADD_METRIC_REQUEST_CODE);
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.i(TAG, "onStop()");

        // TODO: This is shit code. It's O(n^2)
        mDatabaseHelper = new MetricItemDBHelper(this); // Creates database
        mDatabase = mDatabaseHelper.getWritableDatabase();
        mDatabaseHelper.onUpgrade(mDatabase, 1, 1);

        // Drop the table. Re-create. Fill.
        Iterator metricEntryIter = mMetricEntryArrayList.iterator();
        Log.e(TAG, "Size of metricEntryArrayList " + mMetricEntryArrayList.size());
        // mDatabase.execSQL("DROP TABLE IF EXISTS " + MetricItemDBHelper.DATABASE_NAME);

        while (metricEntryIter.hasNext()) {
            MetricEntry metricEntry = (MetricEntry) metricEntryIter.next();
            mDatabaseHelper.addMetricEntry(mDatabase, metricEntry.todo,
                    metricEntry.comment, metricEntry.getDueDate(), metricEntry.getCompletedDate(),
                    metricEntry.getCompleted(), metricEntry.duration);
        }

        Log.i("Metrics", "onStop() - Closing mDatabaseHelper");
        mDatabaseHelper.debugDatabase(mDatabase);
        mDatabaseHelper.close();

    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null) return;
        if (requestCode == 1) {
            if (!data.hasExtra("task")) {
                return;
            }
            Bundle bundle = (Bundle) data.getExtras();
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                // Okay, we should something to our list.
                TableLayout tl = (TableLayout) findViewById(R.id.table_layout);
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
            }
            MetricEntry metricEntry = new MetricEntry(bundle.getString("task"),
                        bundle.getString("comments"),
                        new MetricEntry.SuperSimpleDate(1234567890),
                        new MetricEntry.SuperSimpleDate(0),
                        4,
                        10020);

            mMetricEntryArrayList.add(metricEntry);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mAdapter.notifyDataSetChanged();
            }

        } else if (requestCode == EDIT_REQUEST_CODE ) {
            Log.i(TAG, "Done editing !");
        } else {
            TableLayout tl = (TableLayout) findViewById(R.id.table_layout);
            TextView tv_temp = new TextView(getApplicationContext());
            tv_temp.setText("Added a metric 2");
            tl.addView(tv_temp);
        }
    }


    public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
        private ViewHolder mViewHolder;
        private float downXPointer = 0;
        private final int SWIPE_DELTA = 100;
        private final int CLICK_DELTA = 50;

        // Provide a reference to the views for each data item
        // Complex data items may need more than one view per item, and
        // you provide access to all the views for a data item in a view holder
        public class ViewHolder extends RecyclerView.ViewHolder {
            // each data item is just a string in this case
            public TextView mTextViewTask;
            public TextView mTextViewComment;
            public TextView mTextViewCountdown;
            public TextView mTextViewDate;
            public ViewHolder(View v) {
                super(v);
                Log.d(TAG, "Called super from ViewHolder constructor for layout");
                mTextViewTask = (TextView) v.findViewById(R.id.topleft_todo_row);
                mTextViewDate = (TextView) v.findViewById(R.id.topright_todo_row);
                mTextViewComment = (TextView) v.findViewById(R.id.bottomleft_todo_row);
                mTextViewCountdown = (TextView) v.findViewById(R.id.bottomright_todo_row);
            }
        }

        // Provide a suitable constructor (depends on the kind of dataset)
        public MyAdapter() {}

        // Create new views (invoked by the layout manager)
        @Override
        public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View todo_view = LayoutInflater.from(parent.getContext()).inflate(R.layout.todolist_entry, parent, false);

            // set the view's size, margins, paddings and layout parameters
            // mViewHolder = new ViewHolder(todo_view, due_view, comment_view, duration_view);
            mViewHolder = new ViewHolder(todo_view);
            Log.d(TAG, "Finished onCreate");
            return mViewHolder;
        }

        // Replace the contents of a view (invoked by the layout manager)
        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            // - get element from your dataset at this position
            // - replace the contents of the view with that element

            final MetricEntry metricEntry = mMetricEntryArrayList.get(position);
            holder.mTextViewTask.setText(metricEntry.todo);
            holder.mTextViewComment.setText(metricEntry.comment);
            holder.mTextViewCountdown.setText(Integer.toString(metricEntry.duration));
            holder.mTextViewDate.setText(Integer.toString(metricEntry.getDueDate()));
            // Todo: Change color based on duration left
            final int thisPos = holder.getLayoutPosition();

            holder.itemView.setOnTouchListener(new View.OnTouchListener() {
                public boolean onTouch(View v, MotionEvent ev) {
                    switch (ev.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            Log.i(TAG, "Down");
                            downXPointer = ev.getX(0);
                            break;
                        case MotionEvent.ACTION_UP:
                            Log.i(TAG, "Up");
                            if (ev.getX(0) >= (downXPointer + SWIPE_DELTA)) {
                                // Swipe to the right
                                Calendar c = Calendar.getInstance();
                                metricEntry.setCompletedDate(new MetricEntry.SuperSimpleDate(
                                                c.get(Calendar.YEAR),
                                                c.get(Calendar.MONTH),
                                                c.get(Calendar.DAY_OF_MONTH),
                                                c.get(Calendar.HOUR),
                                                c.get(Calendar.MINUTE)));
                                metricEntry.setCompleted(true);
                                mCompletedMetrics.add(metricEntry);
                                mMetricEntryArrayList.remove(thisPos);
                                mAdapter.notifyDataSetChanged();
                            } else if (ev.getX(0) <= (downXPointer + CLICK_DELTA) &&
                                    ev.getX(0) >= (downXPointer - CLICK_DELTA)) {
                                // Edit
                                Bundle bundle = new Bundle();
                                bundle.putString("todo", metricEntry.todo);
                                bundle.putString("comment", metricEntry.comment);
                                bundle.putInt("duration", metricEntry.duration);
                                bundle.putInt("duedate", metricEntry.getDueDate());
                                bundle.putInt("position", thisPos);

                                Intent addActivityIntent = new Intent(getApplicationContext(), AddMetricActivity.class);
                                addActivityIntent.putExtras(bundle);

                                startActivityForResult(addActivityIntent, EDIT_REQUEST_CODE);
                            } else if (ev.getX(0) <= (downXPointer - SWIPE_DELTA)) {
                                // Swipe to the left
                                mMetricEntryArrayList.remove(thisPos);
                                mAdapter.notifyDataSetChanged();
                            }
                            break;
                        default:
                            break;
                    }
                    return true;
                }
            });
        }



        // Return the size of your dataset (invoked by the layout manager)
        // Theory: LayoutManager uses this value to determine how many times onBindViewHolder
        // gets called
        @Override
        public int getItemCount() {
            return mMetricEntryArrayList.size();
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

        public void addMetricEntry(SQLiteDatabase db, String todo, String comment,
            int due_date, int completed_time, int completed, int duration) {
            db.execSQL("INSERT INTO " + MetricItemDB.MetricItemEntry.TABLE_NAME + " (" +
                    MetricItemDB.MetricItemEntry.COLUMN_NAME_ID + MetricItemDB.COMMA_SEP +
                    MetricItemDB.MetricItemEntry.COLUMN_NAME_TODO + MetricItemDB.COMMA_SEP +
                    MetricItemDB.MetricItemEntry.COLUMN_NAME_COMMENT + MetricItemDB.COMMA_SEP +
                    MetricItemDB.MetricItemEntry.COLUMN_NAME_DUE_DATE + MetricItemDB.COMMA_SEP +
                    MetricItemDB.MetricItemEntry.COLUMN_NAME_COMPLETED_TIME + MetricItemDB.COMMA_SEP +
                    MetricItemDB.MetricItemEntry.COLUMN_NAME_COMPLETED + MetricItemDB.COMMA_SEP +
                    MetricItemDB.MetricItemEntry.COLUMN_NAME_DURATION + ")" +

                    " VALUES (" +
                    "null" + MetricItemDB.COMMA_SEP +
                    "\"" + todo + "\"" + MetricItemDB.COMMA_SEP +
                    "\"" + comment + "\""+ MetricItemDB.COMMA_SEP +
                    due_date + MetricItemDB.COMMA_SEP +
                    completed_time + MetricItemDB.COMMA_SEP +
                    completed + MetricItemDB.COMMA_SEP +
                    duration + ");");
        }

        public void debugDatabase(SQLiteDatabase db) {
            String query = "SELECT * from " + MetricItemDB.MetricItemEntry.TABLE_NAME;
            Cursor c = db.rawQuery(query, null);
            Log.e(TAG, "Cursor count = " + c.getCount());
            c.moveToFirst();

            for (int x=0; x<c.getCount(); x++) {
                Log.e(TAG, c.getString(1) +
                        c.getString(2) +
                        new MetricEntry.SuperSimpleDate(c.getInt(3)) +
                        new MetricEntry.SuperSimpleDate(c.getInt(4)) +
                        c.getInt(5) +
                        c.getInt(6)); // duration

                c.moveToNext();
            }

            c.close();
        }
    }

    /**
     * SQL Database handling for creating tables
     * Shall only handled by MetricItemDBHelper
     */
    public final class MetricItemDB {

        private static final String TEXT_TYPE = " TEXT";
        private static final String PRIMARY_TYPE = " INTEGER PRIMARY KEY";
        private static final String INT_TYPE = " INTEGER KEY";
        private static final String COMMA_SEP = ",";
        private static final String SQL_CREATE_ENTRIES =
                "CREATE TABLE " + MetricItemEntry.TABLE_NAME + " (" +
                        MetricItemEntry.COLUMN_NAME_ID + PRIMARY_TYPE + COMMA_SEP+
                        MetricItemEntry.COLUMN_NAME_TODO + TEXT_TYPE + COMMA_SEP +
                        MetricItemEntry.COLUMN_NAME_COMMENT + TEXT_TYPE + COMMA_SEP +
                        MetricItemEntry.COLUMN_NAME_DUE_DATE + INT_TYPE + COMMA_SEP +
                        MetricItemEntry.COLUMN_NAME_COMPLETED_TIME + INT_TYPE + COMMA_SEP +
                        MetricItemEntry.COLUMN_NAME_COMPLETED + INT_TYPE + COMMA_SEP +
                        MetricItemEntry.COLUMN_NAME_DURATION + INT_TYPE +
                ")";

        private static final String SQL_DELETE_ENTRIES =
                "DROP TABLE IF EXISTS " + MetricItemEntry.TABLE_NAME;

        public void MetricItem(){}

        /* Inner class that defines table contents */
        public abstract class MetricItemEntry implements BaseColumns {
            public static final String TABLE_NAME = "entry"; // Table name
            public static final String COLUMN_NAME_ID = "id"; // Processed as an integer
            public static final String COLUMN_NAME_TODO = "todo"; // String that shouldn't be multi-line
            public static final String COLUMN_NAME_COMMENT = "comment"; // Standard multi-line String
            public static final String COLUMN_NAME_DUE_DATE = "due_date"; // handled like a calendar date
            public static final String COLUMN_NAME_COMPLETED_TIME = "completed_date"; // handled like a calendar date/time
            public static final String COLUMN_NAME_COMPLETED = "Completed"; // handled like a boolean
            public static final String COLUMN_NAME_DURATION = "Duration"; // How long it will take in minutes
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
