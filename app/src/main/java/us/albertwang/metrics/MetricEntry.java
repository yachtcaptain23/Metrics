package us.albertwang.metrics;

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
    public int duration; // Estimated time needed to finish


    public MetricEntry() {}

    public MetricEntry(String todo, String comment,
            SuperSimpleDate due_date, SuperSimpleDate completed_date,
            int isCompleted, int duration) {
        this.todo = todo;
        this.comment = comment;
        this.due_date = due_date;
        this.completed_date = completed_date;
        this.isCompleted = isCompleted == 1;
        this.duration = duration;
    }

    // Simple date format
    public static class SuperSimpleDate {
        //YYMMDDHHMM
        int currentDate;
        public SuperSimpleDate() {
            currentDate = 0;
        }

        public SuperSimpleDate(int ssd) {
            currentDate = ssd;
        }

        public SuperSimpleDate(int year, int month, int day,
            int hour, int minute) {
            currentDate =   minute +
                    (1<<2) * hour +
                    (1<<4) * day +
                    (1<<6) * month +
                    (1<<8) * year;
        }

        public int getCurrentDate() {
            return currentDate;
        }

        public void setCurrentDate(int newDate) {
            currentDate = newDate;
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

    public int getCompleted() {
        return this.isCompleted ? 1 : 0;
    }
}
