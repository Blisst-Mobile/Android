package com.codeday.detroit.taskmanager.app.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by timothymiko on 3/23/14.
 *
 * This class is used to manage the database file specified in DATABASE_NAME. It handles creating
 * and upgrading the database by incrementing DATABASE_VERSION. All tables and columns are
 * specified here as well. Lastly, it manages opening and closes the database file when reading
 * or writing to the database.
 *
 */
public class SQLiteHelper extends SQLiteOpenHelper {

    // Database version
    private static final int DATABASE_VERSION = 1;

    // database name
    public static final String DATABASE_NAME = "data.db";

    public static SQLiteHelper mInstance;

    private static String TAG = "SQLiteHelper";

    private Context mContext = null;

    // table names
    public static final String TABLE_LISTS = "Lists";
    public static final String TABLE_TASKS = "Tasks";

    // Lists columns
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_IDENTIFIER = "identifier";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_TASKS = "numOfTasks";
    public static final String COLUMN_TASKS_COMPLETED = "numOfCompletedTasks";
    public static final String COLUMN_ISCOMPLETE = "isComplete";

    // Task columns
    public static final String COLUMN_PARENT = "parentList";
    public static final String COLUMN_DATE = "endDate";
    public static final String COLUMN_PRIORITY = "priorityLevel";

    // Database creation sql statement for TABLE_LISTS
    private static final String CREATE_LISTS = "create table " + TABLE_LISTS + "(" + COLUMN_ID + " integer primary key autoincrement, "
            + COLUMN_IDENTIFIER + " text not null," + COLUMN_NAME + " text not null, " + COLUMN_TASKS + " integer, " +
            COLUMN_TASKS_COMPLETED + " integer, " + COLUMN_ISCOMPLETE + " integer);";

    // Database creation sql statement for TABLE_TASKS
    private static final String CREATE_TASKS = "create table " + TABLE_TASKS + "(" + COLUMN_ID + " integer primary key autoincrement, "
            + COLUMN_IDENTIFIER + " text not null," + COLUMN_PARENT + " text not null," + COLUMN_NAME + " text not null," + COLUMN_DATE +
            " integer, " + COLUMN_PRIORITY + " integer, " + COLUMN_ISCOMPLETE + " integer);";

    // Accessor strings
    public static final String SQL_WHERE_BY_ID = SQLiteHelper.COLUMN_IDENTIFIER + "=?";
    public static final String SQL_WHERE_BY_PARENT = SQLiteHelper.COLUMN_PARENT + "=?";

    private SQLiteHelper(Context context) {
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
        this.mContext = context;
    }

    public static SQLiteHelper getInstance(Context context) {


        if (mInstance == null) {
            mInstance = new SQLiteHelper(context.getApplicationContext());
        }
        return mInstance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_LISTS);
        db.execSQL(CREATE_TASKS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        // drop old versions of tables if they exist
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LISTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TASKS);

        onCreate(db);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
    }

    /**
     * Opens the database for use
     */
    public SQLiteDatabase open() {
        return getWritableDatabase();
    }

    /**
     * Closes the database
     */
    public void close() {
        close();
    }
}
