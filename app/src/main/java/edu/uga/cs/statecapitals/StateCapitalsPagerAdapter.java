package edu.uga.cs.statecapitals;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.List;

public class StateCapitalsPagerAdapter extends FragmentStateAdapter {

    private static final int NUM_STATES = 50; // or however many entries in your CSV
    private final List<Integer> questionIds;

    public StateCapitalsPagerAdapter(@NonNull FragmentManager fm, @NonNull Lifecycle lifecycle, List<Integer> questionIds) {
        super(fm, lifecycle);
        this.questionIds = questionIds;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return StateCapitalFragment.newInstance(questionIds.get(position));
    }

    @Override
    public int getItemCount() {
        return questionIds.size();
    }
}
