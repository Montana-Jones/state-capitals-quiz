package edu.uga.cs.statecapitals;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

/**
 * Shows a scrollable list of past quiz results using AsyncTask for DB access.
 */
public class PreviousQuizzesActivity extends AppCompatActivity {

    private RecyclerView recycler;
    private TextView emptyView;
    private QuizResultsAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_previous_quizzes);

        recycler = findViewById(R.id.recycler);
        emptyView = findViewById(R.id.emptyView);

        recycler.setLayoutManager(new LinearLayoutManager(this));
        adapter = new QuizResultsAdapter();
        recycler.setAdapter(adapter);

        new LoadResultsTask().execute();
    }

    /** AsyncTask to read results without blocking the UI thread. */
    private class LoadResultsTask extends AsyncTask<Void, Void, ArrayList<QuizResult>> {
        @Override
        protected ArrayList<QuizResult> doInBackground(Void... voids) {
            ArrayList<QuizResult> list = new ArrayList<>();
            StateCapitalsDBHelper h = StateCapitalsDBHelper.getInstance(getApplicationContext());
            SQLiteDatabase db = h.getReadableDatabase();
            Cursor c = db.query(
                    StateCapitalsDBHelper.TABLE_RESULTS,
                    new String[] {
                            StateCapitalsDBHelper.RESULTS_COLUMN_ID,
                            StateCapitalsDBHelper.RESULTS_COLUMN_TAKEN_AT,
                            StateCapitalsDBHelper.RESULTS_COLUMN_SCORE,
                            StateCapitalsDBHelper.RESULTS_COLUMN_TOTAL
                    },
                    null, null, null, null,
                    StateCapitalsDBHelper.RESULTS_COLUMN_TAKEN_AT + " DESC");
            while (c.moveToNext()) {
                long id   = c.getLong(c.getColumnIndexOrThrow(StateCapitalsDBHelper.RESULTS_COLUMN_ID));
                long when = c.getLong(c.getColumnIndexOrThrow(StateCapitalsDBHelper.RESULTS_COLUMN_TAKEN_AT));
                int score = c.getInt(c.getColumnIndexOrThrow(StateCapitalsDBHelper.RESULTS_COLUMN_SCORE));
                int total = c.getInt(c.getColumnIndexOrThrow(StateCapitalsDBHelper.RESULTS_COLUMN_TOTAL));
                list.add(new QuizResult(id, when, score, total));
            }
            c.close(); db.close();
            return list;
        }

        @Override
        protected void onPostExecute(ArrayList<QuizResult> results) {
            if (results == null || results.isEmpty()) {
                recycler.setVisibility(View.GONE);
                emptyView.setVisibility(View.VISIBLE);
            } else {
                emptyView.setVisibility(View.GONE);
                recycler.setVisibility(View.VISIBLE);
                adapter.submit(results);
            }
        }
    }
}
