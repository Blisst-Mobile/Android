package com.codeday.detroit.taskmanager.app.dao;

import com.codeday.detroit.taskmanager.app.domain.Task;
import com.codeday.detroit.taskmanager.app.domain.TaskList;

import java.util.List;

/**
 * Created by timothymiko on 5/24/14.
 *
 * This interface is used to define the methods needed to interact with the database. Any new
 * methods, for example, a method to add multiple tasks at once, should be defined in here. The
 * methods are then implemented in DatabaseAccessor.
 */
public interface Database {

    /**
     * Retrieves list object with the specified identifier
     *
     * @param identifier unique identifier of list to retrieve
     *
     * @return TaskList object
     */
    TaskList getList(String identifier);

    /**
     * Retrieves all TaskList objects
     *
     * @return all TaskList objects
     */
    List<TaskList> getAllLists();

    /**
     * Retrieves Taks object with the specified identifier
     *
     * @param identifier unique identifier of Task to retrieve
     *
     * @return Task object
     */
    Task getTask(String identifier);

    /**
     * Retrieves all Task objects for a TaskList
     *
     * @param identifier identifier of TaskList
     *
     * @return Task objects with parent field equal to identifier
     */
    List<Task> getTasksForList(String identifier);

    /**
     * Adds a new TaskList object
     *
     * @param list TaskList to add to database
     *
     * @return true if successful, false otherwise
     */
    boolean addList(TaskList list);

    /**
     * Adds a new Task object
     *
     * @param task Task to add to database
     *
     * @return true if successful, false otherwise
     */
    boolean addTask(Task task);

    /**
     * Updates an existing TaskList object
     *
     * @param list TaskList to update
     *
     * @return true if successful, false otherwise
     */
    boolean updateList(TaskList list);

    /**
     * Updates an existing task object
     *
     * @param task Task to update
     *
     * @return true if successful, false otherwise
     */
    boolean updateTask(Task task);
}
