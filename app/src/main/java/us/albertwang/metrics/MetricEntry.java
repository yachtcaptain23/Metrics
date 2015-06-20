package us.albertwang.metrics;

import java.util.Calendar;
import java.text.DateFormat;
import java.util.Locale;

/**
 * Created by albewang on 3/1/15.
 * Creates a MetricEntry which is the structure for a particular item
 * Used for front-end and for back-end
 */

public class MetricEntry {
    public String todo;
    public String comment;
    public Calendar due_date;
    public Calendar completed_date;
    public boolean isCompleted;
    public int duration;


    public MetricEntry() {}

    public MetricEntry(String todo, String comment,
            int due_date, int completed_date,
            int isCompleted, int duration) {
        this.todo = todo;
        this.comment = comment;
        this.due_date = setCalendarValue(due_date);
        this.completed_date = setCalendarValue(completed_date);
        this.isCompleted = isCompleted == 1;
        this.duration = duration;
    }

    private Calendar setCalendarValue(int date) {
        Calendar cal = Calendar.getInstance();
        int year = date / 100000000;
        int month = date / 1000000 % 100;
        int day = date / 10000 % 100;
        int hour = date / 100 % 100;
        int minute = date % 100;
        cal.set(year, month, day, hour, minute);
        return cal;
    }
}
