package edu.uga.cs.statecapitals;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import java.util.List;

public class StateCapitalsPagerAdapter extends FragmentStateAdapter {

    private final List<Integer> questionIds;

    public StateCapitalsPagerAdapter(@NonNull FragmentManager fm, @NonNull Lifecycle lifecycle, List<Integer> questionIds) {
        super(fm, lifecycle);
        this.questionIds = questionIds;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return StateCapitalFragment.newInstance(questionIds.get(position), position + 1);
    }

    @Override
    public int getItemCount() {
        return questionIds.size();
    }
}
