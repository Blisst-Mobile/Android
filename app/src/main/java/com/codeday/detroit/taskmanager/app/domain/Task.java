package com.codeday.detroit.taskmanager.app.domain;

import java.util.Calendar;
import java.util.Comparator;
import java.util.UUID;

/**
 * Created by timothymiko on 5/24/14.
 */
public class Task implements Comparable<Task> {

    public String identifier;
    public String parent;
    public String name;
    public boolean priority;
    public Calendar date;
    public boolean isComplete;

    public Task() {
        this.identifier = UUID.randomUUID().toString();
    }

    public Task(String parent, String name, boolean priority, Calendar date, boolean isComplete) {
        this.identifier = UUID.randomUUID().toString();
        this.parent = parent;
        this.name = name;
        this.priority = priority;
        this.date = date;
        this.isComplete = isComplete;
    }

    public Task(String identifier, String parent, String name, boolean priority, Calendar date, boolean isComplete) {
        this.identifier = identifier;
        this.parent = parent;
        this.name = name;
        this.priority = priority;
        this.date = date;
        this.isComplete = isComplete;
    }

    @Override
    public int compareTo(Task task) {
        return this.date.compareTo(task.date);
    }
}
