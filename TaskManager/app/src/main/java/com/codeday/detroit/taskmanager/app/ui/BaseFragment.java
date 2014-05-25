package com.codeday.detroit.taskmanager.app.ui;


import android.app.Activity;
import android.support.v4.app.Fragment;

import com.codeday.detroit.taskmanager.app.MainActivity;

/**
 * Created by timothymiko on 5/24/14.
 */
public class BaseFragment extends Fragment {

    MainActivity.MenuInteractionListener menuInteractionListener;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainActivity) activity).menuInteractionListener = menuInteractionListener;
    }
}
