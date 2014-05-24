package com.codeday.detroit.taskmanager.app.domain;

import java.util.Calendar;

/**
 * Created by timothymiko on 5/24/14.
 */
public class Task {

    public String identifier;
    public String parent;
    public String name;
    public boolean priority;
    public Calendar date;
    public boolean isComplete;

}
