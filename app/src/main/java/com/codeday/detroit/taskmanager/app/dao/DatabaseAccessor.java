package com.codeday.detroit.taskmanager.app.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.codeday.detroit.taskmanager.app.GlobalContext;
import com.codeday.detroit.taskmanager.app.domain.Task;
import com.codeday.detroit.taskmanager.app.domain.TaskList;
import com.codeday.detroit.taskmanager.app.util.ConversionUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by timothymiko on 5/24/14.
 *
 * This class is used to interact with the database. It handles adding and retrieving data from
 * the database. The methods in this class should not be run on the main UI thread. They should
 * be run from a separate Thread or in an AsyncTask.
 */
public class DatabaseAccessor implements Database {


    @Override
    public TaskList getList(String identifier) {
        SQLiteDatabase db = SQLiteHelper.getInstance(GlobalContext.getAppContext()).getReadableDatabase();

        TaskList taskList = null;

        db.beginTransaction();

        try {

            Cursor cursor = db.query(SQLiteHelper.TABLE_LISTS, null, SQLiteHelper.SQL_WHERE_BY_ID, new String[]{identifier}, null, null, null);

            if ((cursor != null) && (cursor.getCount() > 0)) {
                cursor.moveToFirst();
                taskList = ConversionUtil.cursorToTaskList(cursor);
                cursor.close();
                db.setTransactionSuccessful();
            }

        } finally {
            db.endTransaction();
        }

        return taskList;
    }

    @Override
    public List<TaskList> getAllLists() {

        SQLiteDatabase db = SQLiteHelper.getInstance(GlobalContext.getAppContext()).getReadableDatabase();

        ArrayList<TaskList> values = null;

        db.beginTransaction();

        try {

            Cursor cursor = db.rawQuery("select * from " + SQLiteHelper.TABLE_LISTS, null);

            // Check to make sure that the query returned anything
            if ((cursor != null) && (cursor.getCount() > 0)) {

                cursor.moveToFirst();

                values = new ArrayList<TaskList>();

                while (!cursor.isAfterLast()) {
                    values.add(ConversionUtil.cursorToTaskList(cursor));
                    cursor.moveToNext();
                }

                Collections.sort(values);

                cursor.close();
                db.setTransactionSuccessful();
            }
        } finally {
            db.endTransaction();
        }

        return values;
    }

    @Override
    public Task getTask(String identifier) {
        SQLiteDatabase db = SQLiteHelper.getInstance(GlobalContext.getAppContext()).getReadableDatabase();

        Task task = null;

        db.beginTransaction();
        try {

            Cursor cursor = db.query(SQLiteHelper.TABLE_LISTS, null, SQLiteHelper.SQL_WHERE_BY_ID, new String[]{identifier}, null, null, null);

            if ((cursor != null) && (cursor.getCount() > 0)) {
                cursor.moveToFirst();
                task = ConversionUtil.cursorToTask(cursor);
                cursor.close();
                db.setTransactionSuccessful();
            }

        } finally {
            db.endTransaction();
        }

        return task;
    }

    @Override
    public List<Task> getTasksForList(String identifier) {

        // Open the database
        SQLiteDatabase db = SQLiteHelper.getInstance(GlobalContext.getAppContext()).getReadableDatabase();

        // Create new map object
        ArrayList<Task> values = null;

        db.beginTransaction();
        try {

            // Query the database
            Cursor cursor = db.rawQuery("select * from " + SQLiteHelper.TABLE_TASKS + " where " + SQLiteHelper.COLUMN_PARENT + " ='" + identifier + "'", null);

            // Check to make sure that the query returned anything
            if ((cursor != null) && (cursor.getCount() > 0)) {

                cursor.moveToFirst();

                values = new ArrayList<Task>();

                while (!cursor.isAfterLast()) {
                    values.add(ConversionUtil.cursorToTask(cursor));
                    cursor.moveToNext();
                }

                Collections.sort(values);

                cursor.close();
                db.setTransactionSuccessful();
            }

        } finally {
            db.endTransaction();
        }

        return values;
    }

    @Override
    public boolean addList(TaskList list) {
        SQLiteDatabase db = SQLiteHelper.getInstance(GlobalContext.getAppContext()).getWritableDatabase();
        db.beginTransaction();
        long result = 0;
        try {
            ContentValues args = ConversionUtil.taskListToContentValues(list);

            result = db.insert(SQLiteHelper.TABLE_LISTS, null, args);

            db.setTransactionSuccessful();

        } finally {
            db.endTransaction();
        }

        return result > 0;
    }

    @Override
    public boolean addTask(Task task) {
        SQLiteDatabase db = SQLiteHelper.getInstance(GlobalContext.getAppContext()).getWritableDatabase();
        db.beginTransaction();
        long result = 0;
        try {

            ContentValues args = ConversionUtil.taskToContentValues(task);

            result = db.insert(SQLiteHelper.TABLE_TASKS, null, args);

            db.setTransactionSuccessful();

        } finally {
            db.endTransaction();
        }

        return result > 0;
    }

    @Override
    public boolean updateList(TaskList list) {
        SQLiteDatabase db = SQLiteHelper.getInstance(GlobalContext.getAppContext()).getWritableDatabase();
        db.beginTransaction();
        int result = 0;
        try {

            ContentValues values = ConversionUtil.taskListToContentValues(list);

            result = db.update(SQLiteHelper.TABLE_LISTS, values, SQLiteHelper.SQL_WHERE_BY_ID, new String[]{list.identifier});

            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }

        return result > 0;
    }

    @Override
    public boolean updateTask(Task task) {
        SQLiteDatabase db = SQLiteHelper.getInstance(GlobalContext.getAppContext()).getWritableDatabase();
        db.beginTransaction();
        int result = 0;
        try {

            ContentValues values = ConversionUtil.taskToContentValues(task);

            result = db.update(SQLiteHelper.TABLE_TASKS, values, SQLiteHelper.SQL_WHERE_BY_ID, new String[]{task.identifier});

            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }

        return result > 0;
    }

    @Override
    public boolean deleteList(String identifier) {
        SQLiteDatabase db = SQLiteHelper.getInstance(GlobalContext.getAppContext()).getWritableDatabase();
        db.beginTransaction();
        int result = 0;
        try {

            result = db.delete(SQLiteHelper.TABLE_LISTS, SQLiteHelper.SQL_WHERE_BY_ID, new String[]{identifier});

            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }

        return result > 0;
    }

    @Override
    public boolean deleteLists(List<String> identifiers) {
        SQLiteDatabase db = SQLiteHelper.getInstance(GlobalContext.getAppContext()).getWritableDatabase();
        db.beginTransaction();
        int result = 0;
        try {

            result = db.delete(SQLiteHelper.TABLE_LISTS, SQLiteHelper.SQL_WHERE_BY_ID, (String[]) identifiers.toArray());

            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }

        return result > 0;
    }

    @Override
    public boolean deleteTask(String identifier) {
        SQLiteDatabase db = SQLiteHelper.getInstance(GlobalContext.getAppContext()).getWritableDatabase();
        db.beginTransaction();
        int result = 0;
        try {

            result = db.delete(SQLiteHelper.TABLE_TASKS, SQLiteHelper.SQL_WHERE_BY_ID, new String[]{identifier});

            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }

        return result > 0;
    }

    @Override
    public boolean deleteTasks(List<String> identifiers) {
        SQLiteDatabase db = SQLiteHelper.getInstance(GlobalContext.getAppContext()).getWritableDatabase();
        db.beginTransaction();
        int result = 0;
        try {

            result = db.delete(SQLiteHelper.TABLE_TASKS, SQLiteHelper.SQL_WHERE_BY_ID, (String[]) identifiers.toArray());

            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }

        return result > 0;
    }

    @Override
    public boolean deleteAllTasksForList(String identifier) {
        SQLiteDatabase db = SQLiteHelper.getInstance(GlobalContext.getAppContext()).getWritableDatabase();
        db.beginTransaction();
        int result = 0;
        try {

            result = db.delete(SQLiteHelper.TABLE_TASKS, SQLiteHelper.SQL_WHERE_BY_ID, new String[]{identifier});

            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }

        return result > 0;
    }

}
