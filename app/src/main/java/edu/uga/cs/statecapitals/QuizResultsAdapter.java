package edu.uga.cs.statecapitals;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/** RecyclerView adapter to show quiz results in cards. */
public class QuizResultsAdapter extends RecyclerView.Adapter<QuizResultsAdapter.VH> {

    private final List<QuizResult> items = new ArrayList<>();
    private final DateFormat fmt = DateFormat.getDateTimeInstance(
            DateFormat.MEDIUM, DateFormat.SHORT);

    public void submit(List<QuizResult> list) {
        items.clear();
        if (list != null) items.addAll(list);
        notifyDataSetChanged();
    }

    @NonNull
    @Override public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_quiz_result, parent, false);
        return new VH(v);
    }

    @Override public void onBindViewHolder(@NonNull VH h, int pos) {
        QuizResult r = items.get(pos);
        h.dateText.setText(fmt.format(new Date(r.takenAtMillis)));
        h.scoreText.setText("Score: " + r.score + " / " + r.total);
        h.detailText.setText("Tap to view details");
    }

    @Override public int getItemCount() { return items.size(); }

    static class VH extends RecyclerView.ViewHolder {
        final TextView dateText, scoreText, detailText;
        VH(@NonNull View itemView) {
            super(itemView);
            dateText = itemView.findViewById(R.id.dateText);
            scoreText = itemView.findViewById(R.id.scoreText);
            detailText = itemView.findViewById(R.id.detailText);
        }
    }
}
