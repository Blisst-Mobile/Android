package com.codeday.detroit.taskmanager.app.util;

import android.content.ContentValues;
import android.database.Cursor;

import com.codeday.detroit.taskmanager.app.dao.SQLiteHelper;
import com.codeday.detroit.taskmanager.app.domain.Task;
import com.codeday.detroit.taskmanager.app.domain.TaskList;

import java.util.Calendar;

/**
 * Created by timothymiko on 5/24/14.
 *
 * This class is used to convert classes specified in the domain package into various objects
 * such as a Bundle or Cursor. It can be used to extract data from those objects as well.
 *
 */
public class ConversionUtil {

    public static ContentValues taskListToContentValues(TaskList taskList) {

        ContentValues args = new ContentValues();
        args.put(SQLiteHelper.COLUMN_IDENTIFIER, taskList.identifier);
        args.put(SQLiteHelper.COLUMN_NAME, taskList.name);
        args.put(SQLiteHelper.COLUMN_TASKS, taskList.numberOfTasks);
        args.put(SQLiteHelper.COLUMN_TASKS_COMPLETED, taskList.numberOfCompletedTasks);
        args.put(SQLiteHelper.COLUMN_ISCOMPLETE, taskList.isComplete ? 1 : 0);

        return args;
    }

    public static TaskList cursorToTaskList(Cursor cursor) {

        TaskList list = new TaskList();
        list.identifier = cursor.getString(0);
        list.name = cursor.getString(1);
        list.numberOfTasks = cursor.getInt(2);
        list.numberOfCompletedTasks = cursor.getInt(3);
        list.isComplete = cursor.getInt(4) == 1;

        return list;
    }

    public static ContentValues taskToContentValues(Task task) {

        ContentValues args = new ContentValues();
        args.put(SQLiteHelper.COLUMN_IDENTIFIER, task.identifier);
        args.put(SQLiteHelper.COLUMN_PARENT, task.parent);
        args.put(SQLiteHelper.COLUMN_NAME, task.name);
        args.put(SQLiteHelper.COLUMN_PRIORITY, task.priority ? 1 : 0);
        args.put(SQLiteHelper.COLUMN_DATE, task.date.getTimeInMillis());
        args.put(SQLiteHelper.COLUMN_ISCOMPLETE, task.isComplete ? 1 : 0);

        return args;
    }

    public static Task cursorToTask(Cursor cursor) {

        Task task = new Task();
        task.identifier = cursor.getString(0);
        task.parent = cursor.getString(1);
        task.name = cursor.getString(2);
        task.priority = cursor.getInt(3) == 1;

        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(cursor.getLong(4));

        task.date = cal;
        task.isComplete = cursor.getInt(5) == 1;

        return task;
    }
}
