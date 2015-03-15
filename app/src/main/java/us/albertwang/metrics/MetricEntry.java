package us.albertwang.metrics;

import java.util.Calendar;

/**
 * Created by albewang on 3/1/15.
 * Creates a MetricEntry which is the structure for a particular item
 * Used for front-end and for back-end
 */

public class MetricEntry {
    private String todo;
    private String comment;
    private Calendar due_date;
    private Calendar completed_date;
    private boolean isCompleted;
    private int difficulty;

    public void MetricEntry() {}


}
