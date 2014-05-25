package com.codeday.detroit.taskmanager.app;

import android.util.Log;

/**
 * Created by timothymiko on 5/24/14.
 *
 * This class is a wrapper for all log operations. It contains a boolean to easily turn off
 * all logging.
 */
public class CDLog {

    public static boolean loggingEnabled = true;

    public static void verboseLog(String tag, String message) {
        if ( loggingEnabled )
            Log.v(tag, message);
    }

    public static void debugLog(String tag, String message) {
        if ( loggingEnabled )
            Log.d(tag, message);
    }

    public static void infoLog(String tag, String message) {
        if ( loggingEnabled )
            Log.i(tag, message);
    }

    public static void warnLog(String tag, String message) {
        if ( loggingEnabled )
            Log.w(tag, message);
    }

    public static void errorLog(String tag, String message) {
        if ( loggingEnabled )
            Log.e(tag, message);
    }
}
