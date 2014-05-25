package com.codeday.detroit.taskmanager.app.domain;

import java.util.UUID;

/**
 * Created by timothymiko on 5/24/14.
 */
public class TaskList implements Comparable<TaskList> {

    public String identifier;
    public String name;
    public int numberOfTasks;
    public int numberOfCompletedTasks;
    public boolean isComplete;

    public TaskList() {
        this.identifier = UUID.randomUUID().toString();
    }

    public TaskList(String name, int numberOfTasks, int numberOfCompletedTasks, boolean isComplete) {
        this.identifier = UUID.randomUUID().toString();
        this.name = name;
        this.numberOfTasks = numberOfTasks;
        this.numberOfCompletedTasks = numberOfCompletedTasks;
        this.isComplete = isComplete;
    }

    public TaskList(String identifier, String name, int numberOfTasks, int numberOfCompletedTasks, boolean isComplete) {
        this.identifier = identifier;
        this.name = name;
        this.numberOfTasks = numberOfTasks;
        this.numberOfCompletedTasks = numberOfCompletedTasks;
        this.isComplete = isComplete;
    }

    @Override
    public int compareTo(TaskList taskList) {
        return this.name.compareToIgnoreCase(taskList.name);
    }
}
