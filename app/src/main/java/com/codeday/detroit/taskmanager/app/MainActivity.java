package com.codeday.detroit.taskmanager.app;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.MenuItem;

import com.codeday.detroit.taskmanager.app.ui.TaskListFragment;


public class MainActivity extends FragmentActivity {


    public interface MenuInteractionListener {
        void onAddButtonPressed();
        boolean onBackButtonPressed();
        boolean onMenuUpPressed();
    }

    public MenuInteractionListener menuInteractionListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            TaskListFragment frag = TaskListFragment.getInstance();
            transaction.add(R.id.container, frag, TaskListFragment.TAG);
            transaction.commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id) {
            case R.id.action_add:
                if ( menuInteractionListener != null )
                    menuInteractionListener.onAddButtonPressed();
                return true;
            case android.R.id.home:
                if ( menuInteractionListener.onMenuUpPressed() )
                    return true;
                else
                    break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        // reduces overdraw
        if (hasFocus)
            getWindow().setBackgroundDrawable(null);
    }

    @Override
    public void onBackPressed() {
        if (!menuInteractionListener.onBackButtonPressed())
            super.onBackPressed();
    }
}
