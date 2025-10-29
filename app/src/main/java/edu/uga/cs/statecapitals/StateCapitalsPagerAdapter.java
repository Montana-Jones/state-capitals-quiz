package edu.uga.cs.statecapitals;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class StateCapitalsPagerAdapter extends FragmentStateAdapter {

    private static final int NUM_STATES = 50; // or however many entries in your CSV

    public StateCapitalsPagerAdapter(@NonNull FragmentManager fm, @NonNull Lifecycle lifecycle) {
        super(fm, lifecycle);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return StateCapitalFragment.newInstance(position);
    }

    @Override
    public int getItemCount() {
        return NUM_STATES;
    }
}
