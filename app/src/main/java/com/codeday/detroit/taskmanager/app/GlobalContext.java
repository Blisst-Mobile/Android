package com.codeday.detroit.taskmanager.app;

import android.app.Application;
import android.content.Context;

/**
 * Created by timothymiko on 5/24/14.
 *
 * This class is created when the app is started and lives until the app is removed from memory.
 * Any variables specified in this class will be accessible from anywhere during the lifecycle
 * of the application.
 *
 */
public class GlobalContext extends Application {

    private static Context appContext = null;

    @Override
    public void onCreate() {
        super.onCreate();
        GlobalContext.appContext = getApplicationContext();
    }

    public static Context getAppContext() {
        return appContext;
    }
}
