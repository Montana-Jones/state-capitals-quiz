package edu.uga.cs.statecapitals;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class StateCapitalsPagerAdapter extends FragmentStateAdapter {
    public StateCapitalsPagerAdapter(
            FragmentManager fragmentManager,
            Lifecycle lifecycle ) {
        super(fragmentManager, lifecycle);
    }

    @Override
    public Fragment createFragment(int position){
        return StateCapitalFragment
                .newInstance( position );
    }

    @Override
    public int getItemCount() {
        return StateCapitalFragment
                .getNumberOfStates();
    }

}
