package us.albertwang.metrics;

import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.text.DateFormat;
import java.util.Locale;

/**
 * Store information of a single task as a MetricEntry
 */

public class MetricEntry {
    public String todo; // Headline
    public String comment; // Description
    public SuperSimpleDate due_date; // Estimated completion time
    public SuperSimpleDate completed_date;
    public boolean isCompleted;
    public int estimatedCompletionTime; // Estimated time needed to finish


    public MetricEntry() {}

    public MetricEntry(String todo, String comment,
            SuperSimpleDate due_date, SuperSimpleDate completed_date,
            int isCompleted, int estimatedCompletionTime) {
        this.todo = todo;
        this.comment = comment;
        this.due_date = due_date;
        this.completed_date = completed_date;
        this.isCompleted = isCompleted == 1;
        this.estimatedCompletionTime = estimatedCompletionTime;
    }

    // Simple date format
    public static class SuperSimpleDate {
        //YYMMDDHHMM
        int currentDate, year, month, day, hour, minute;
        public SuperSimpleDate() {
            currentDate = 0;
        }

        public SuperSimpleDate(int ssd) {
            currentDate = ssd;
        }

        public SuperSimpleDate(int _year, int _month, int _day,
            int _hour, int _minute) {
            currentDate =   _minute +
                    (1<<2) * _hour +
                    (1<<4) * _day +
                    (1<<6) * _month +
                    (1<<8) * _year;
            year = _year;
            month = _month;
            day = _day;
            hour = _hour;
            minute = _minute;
        }

        public int getCurrentDate() {
            return currentDate;
        }

        public void setCurrentDate(int newDate) {
            currentDate = newDate;
            minute = newDate % 100;
            hour = (newDate >> 2) % 100;
            day = (newDate >> 4) % 100;
            month = (newDate >> 6) % 100;
            year = (newDate >> 8);
        }

        public static String getMonth(int m) {
            switch (m) {
                case 1: return "Jan.";
                case 2: return "Feb.";
                case 3: return "Mar.";
                case 4: return "Apr.";
                case 5: return "May";
                case 6: return "June";
                case 7: return "July";
                case 8: return "Aug.";
                case 9: return "Sept.";
                case 10: return "Oct.";
                case 11: return "Nov.";
                default: return "Dec";
            }
        }

        /**
         * Pretty format informs the due date based on a diff between current time
         * and due time rounded the largest time segment (e.g. X Years, Y Months, Z Days)
         */
        public static String getDueDatePrettyFormat() {
            Calendar cal = Calendar.getInstance();
            cal.getTime();
            // DateFormat df = new SimpleDateFormat("dd/MM/yy HH:mm:ss");
            DateFormat df = new SimpleDateFormat("yy");
            Log.i("albewang", "month: " + df.format(cal.getTime()));
            df = new SimpleDateFormat("MM");
            Log.i("albewang", "month: " + df.format(cal.getTime()));
            df = new SimpleDateFormat("dd");
            Log.i("albewang", "day: " + df.format(cal.getTime()));
            return "";
        }

        public String completedDatePrettyFormat() {
            Calendar cal = Calendar.getInstance();
            return "";
        }
    }

    public int getDueDate() {
        return this.due_date.getCurrentDate();
    }

    public void setDueDate(SuperSimpleDate dueDate) {
        this.due_date = dueDate;
    }

    public int getCompletedDate() {
        return this.completed_date.getCurrentDate();
    }

    public void setCompletedDate(SuperSimpleDate completedDate) {
        this.completed_date = completedDate;
    }

    public void setCompleted(boolean c) {
        this.isCompleted = c;
    }

    /**
     * Used for storing completion information in the SQL db.
     * @return
     */
    public int getCompleted() {
        return this.isCompleted ? 1 : 0;
    }
}
