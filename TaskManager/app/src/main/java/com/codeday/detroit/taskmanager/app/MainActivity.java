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
    }

    MenuInteractionListener menuInteractionListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            TaskListFragment frag = TaskListFragment.getInstance();
            menuInteractionListener = frag.getMenuInteractionListener();
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
        if (id == R.id.action_add) {
            if ( menuInteractionListener != null )
                menuInteractionListener.onAddButtonPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
