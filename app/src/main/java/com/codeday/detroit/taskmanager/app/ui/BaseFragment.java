package com.codeday.detroit.taskmanager.app.ui;


import android.support.v4.app.Fragment;
import android.view.animation.Animation;

import com.codeday.detroit.taskmanager.app.MainActivity;

/**
 * Created by timothymiko on 5/24/14.
 */
public class BaseFragment extends Fragment {

    MainActivity.MenuInteractionListener menuInteractionListener;

    @Override
    public Animation onCreateAnimation(int transit, boolean enter, int nextAnim) {
        Animation anim = super.onCreateAnimation(transit, enter, nextAnim);

        if (enter)
            ((MainActivity) getActivity()).menuInteractionListener = menuInteractionListener;

        return anim;
    }
}
