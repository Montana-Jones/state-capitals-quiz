package edu.uga.cs.statecapitals;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.List;

/**
 * Provides 6 question pages + 1 result page (index 6).
 * Reuses a single ResultFragment instance so that score can be updated immediately.
 */
public class StateCapitalsPagerAdapter extends FragmentStateAdapter {

    private final List<Integer> questionIds;
    private Integer finalScore = null;

    // Keep a single instance of the result fragment so we can push updates.
    private ResultFragment resultFragment;

    public StateCapitalsPagerAdapter(@NonNull FragmentManager fm,
                                     @NonNull Lifecycle lifecycle,
                                     @NonNull List<Integer> questionIds) {
        super(fm, lifecycle);
        this.questionIds = questionIds;
    }

    @NonNull
    @Override public Fragment createFragment(int position) {
        if (position < questionIds.size()) {
            return StateCapitalFragment.newInstance(position, questionIds.get(position));
        } else {
            // Create (or reuse) the result page.
            if (resultFragment == null) {
                int scoreToShow = (finalScore != null) ? finalScore : -1;
                resultFragment = ResultFragment.newInstance(scoreToShow);
            }
            return resultFragment;
        }
    }

    @Override public int getItemCount() {
        return questionIds.size() + 1; // +1 for the result page
    }

    /** Called by MainActivity when the score is ready. */
    public void setScoreAndNotify(int score) {
        this.finalScore = score;
        if (resultFragment != null) {
            // Push the score directly so UI updates immediately.
            resultFragment.setScore(score);
        }
        // Also request a fragment rebind for safety.
        notifyItemChanged(questionIds.size());
    }
}
