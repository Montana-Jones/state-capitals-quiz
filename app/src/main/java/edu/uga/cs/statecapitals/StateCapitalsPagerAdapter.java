package edu.uga.cs.statecapitals;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import java.util.List;

public class StateCapitalsPagerAdapter extends FragmentStateAdapter {

    private final List<Integer> questionIds;
    private Integer score = null;

    public StateCapitalsPagerAdapter(@NonNull FragmentManager fm,
                                     @NonNull Lifecycle lc,
                                     List<Integer> questionIds) {
        super(fm, lc);
        this.questionIds = questionIds;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position < questionIds.size()) {
            return StateCapitalFragment.newInstance(questionIds.get(position), position + 1);
        } else {
            // Always create a fresh ResultFragment with the current score
            int shownScore = (score == null) ? -1 : score;
            return ResultFragment.newInstance(shownScore);
        }
    }

    @Override
    public int getItemCount() {
        return questionIds.size() + 1;
    }

    // Called when quiz is done
    public void setScoreAndNotify(int newScore) {
        this.score = newScore;
        int resultIndex = questionIds.size();

        // This tells ViewPager that the fragment at this position has changed
        notifyItemChanged(resultIndex);
    }

    @Override
    public long getItemId(int position) {
        // Make the ID depend on the score so the result fragment is recreated when score changes
        if (position == questionIds.size() && score != null) {
            return position * 1000L + score;
        }
        return position;
    }

    @Override
    public boolean containsItem(long itemId) {
        int resultIndex = questionIds.size();
        if (score != null && itemId == resultIndex * 1000L + score) return true;
        return itemId < getItemCount();
    }
}
