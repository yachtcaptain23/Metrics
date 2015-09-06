package us.albertwang.metrics;

import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.text.DateFormat;
import java.util.GregorianCalendar;
import java.util.Locale;

/**
 * Store information of a single task as a MetricEntry
 */

public class MetricEntry {
    public String todo; // Headline
    public String comment; // Description
    public GregorianCalendar due_date; // Estimated completion time
    public GregorianCalendar completed_date;
    public boolean isCompleted;
    public int laborType;
    public int estimatedCompletionTime; // Estimated time needed to finish

    public final static int MANUAL_LABOR = 0;
    public final static int CREATIVE_LABOR = 1;

    public MetricEntry() {}

    public MetricEntry(String todo, String comment,
            String dueDate, String dueTime, String completedDate, String completedTime,
            int isCompleted, int laborType, int estimatedCompletionTime) {
        this.todo = todo;
        this.comment = comment;



        Log.i("albewang", "" + dueDate);
        Log.i("albewang", "" + dueTime);
        this.due_date = new GregorianCalendar();
        this.due_date.set(
                Integer.parseInt(dueDate.split("/")[0]), // year
                Integer.parseInt(dueDate.split("/")[1]), // month
                Integer.parseInt(dueDate.split("/")[2]), // day
                Integer.parseInt(dueTime.split(":")[0]), // hour
                Integer.parseInt(dueTime.split(":")[1]) // minute
                );

        this.completed_date = new GregorianCalendar();
        this.completed_date.set(
                Integer.parseInt(completedDate.split("/")[0]), // year
                Integer.parseInt(completedDate.split("/")[1]), // month
                Integer.parseInt(completedDate.split("/")[2]), // day
                Integer.parseInt(completedTime.split(":")[0]), // hour
                Integer.parseInt(completedTime.split(":")[1]) // minute
        );

        this.isCompleted = isCompleted == 1;
        this.laborType = laborType;
        this.estimatedCompletionTime = estimatedCompletionTime;
    }

    /**
     * @return YYYYMMDDHHmm
     */
    public String getDueFormatted() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmm");
        return sdf.format(this.due_date.getTime());
    }

    public String getDueDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        return sdf.format(this.due_date.getTime());
    }

    public String getDueTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        return sdf.format(this.due_date.getTime());
    }

    public String getCompletedFormatted() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmm");
        return sdf.format(this.completed_date.getTime());
    }

    public String getCompletedDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        return sdf.format(this.completed_date.getTime());
    }

    public void setCompletedDate(Calendar completedDate) {
        this.completed_date = (GregorianCalendar) completedDate;
    }

    public void setCompleted(boolean c) {
        this.isCompleted = c;
    }

    public int getLaborType() { return this.laborType; }

    /**
     * Used for storing completion information in the SQL db.
     * @return
     */
    public int getCompleted() {
        return this.isCompleted ? 1 : 0;
    }
}
