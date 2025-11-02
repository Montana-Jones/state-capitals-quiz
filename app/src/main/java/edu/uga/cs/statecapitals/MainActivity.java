package edu.uga.cs.statecapitals;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;

/**
 * MainActivity hosts the entire quiz flow.
 * <p>
 * It shows six questions in a {@link ViewPager2} followed by a result page.
 * User selections are kept in memory and persisted across pauses with
 * {@link SharedPreferences}. If launched with the intent extra "NEW_QUIZ"=true,
 * any previously stored quiz state is ignored and a fresh quiz is created.
 */
public class MainActivity extends AppCompatActivity {

    // SharedPreferences keys
    private static final String PREFS = "quiz_prefs";
    private static final String KEY_CURRENT_INDEX = "current_index";
    private static final String KEY_SELECTED = "selected_";
    private static final String KEY_CORRECT  = "correct_";

    private static final int TOTAL_QUESTIONS = 6;

    private ViewPager2 pager;
    private StateCapitalsPagerAdapter adapter;

    // Selected 6 question row-ids from DB
    private ArrayList<Integer> questionIds = new ArrayList<>();
    // In-memory answer tracking
    private ArrayList<String> selectedAnswers = new ArrayList<>();
    private ArrayList<String> correctAnswers  = new ArrayList<>();

    private boolean resultStored = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Ensure the DB has questions; load CSV on first run.
        ensureQuestionsLoaded(this);

        // Choose 6 random questions for this run.
        questionIds = getRandomQuestionIds(this, TOTAL_QUESTIONS);

        // Prepare answer arrays
        selectedAnswers.ensureCapacity(TOTAL_QUESTIONS);
        correctAnswers .ensureCapacity(TOTAL_QUESTIONS);
        for (int i = 0; i < TOTAL_QUESTIONS; i++) {
            selectedAnswers.add(null);
            correctAnswers .add(null);
        }

        pager = findViewById(R.id.viewpager);
        adapter = new StateCapitalsPagerAdapter(
                getSupportFragmentManager(),
                getLifecycle(),
                questionIds
        );
        pager.setAdapter(adapter);

        // When the result page becomes visible, compute and persist the score once.
        pager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override public void onPageSelected(int position) {
                if (position == TOTAL_QUESTIONS && !resultStored) {
                    resultStored = true;
                    int score = calculateScore();
                    adapter.setScoreAndNotify(score);
                    new QuizResultTask(MainActivity.this, score, TOTAL_QUESTIONS).execute();
                }
            }
        });

        // Rotation/Process-restart safety (we still prefer restoring in onResume).
        if (savedInstanceState != null) {
            pager.setCurrentItem(savedInstanceState.getInt("currentPosition", 0), false);
            selectedAnswers = savedInstanceState.getStringArrayList("selectedAnswers");
            correctAnswers  = savedInstanceState.getStringArrayList("correctAnswers");
        }
    }

    /**
     * Called by each question fragment when the user selects an answer.
     *
     * @param questionIndex index of the current question (0..5)
     * @param selected      the text of the chosen option
     * @param correct       the correct capital text
     */
    public void saveAnswer(int questionIndex, String selected, String correct) {
        if (questionIndex >= 0 && questionIndex < TOTAL_QUESTIONS) {
            selectedAnswers.set(questionIndex, selected);
            correctAnswers .set(questionIndex, correct);
        }
    }

    /** Returns the current score computed from the in-memory selections. */
    public int getCurrentScore() {
        return calculateScore();
    }

    /** Calculates how many answers are currently correct. */
    private int calculateScore() {
        int score = 0;
        for (int i = 0; i < TOTAL_QUESTIONS; i++) {
            if (selectedAnswers.get(i) != null &&
                    selectedAnswers.get(i).equals(correctAnswers.get(i))) {
                score++;
            }
        }
        return score;
    }

    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences sp = getSharedPreferences(PREFS, MODE_PRIVATE);
        SharedPreferences.Editor ed = sp.edit();
        ed.putInt(KEY_CURRENT_INDEX, pager.getCurrentItem());
        for (int i = 0; i < TOTAL_QUESTIONS; i++) {
            ed.putString(KEY_SELECTED + i, selectedAnswers.get(i));
            ed.putString(KEY_CORRECT  + i, correctAnswers.get(i));
        }
        ed.apply();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // If this is a brand-new quiz request from the splash, ignore stored state.
        boolean isNewQuiz = getIntent().getBooleanExtra("NEW_QUIZ", false);
        if (isNewQuiz) {
            getSharedPreferences(PREFS, MODE_PRIVATE).edit().clear().apply();
            pager.post(() -> pager.setCurrentItem(0, false));
            return;
        }

        // Otherwise restore saved answers and the current page index.
        SharedPreferences sp = getSharedPreferences(PREFS, MODE_PRIVATE);
        for (int i = 0; i < TOTAL_QUESTIONS; i++) {
            String sel = sp.getString(KEY_SELECTED + i, selectedAnswers.get(i));
            String cor = sp.getString(KEY_CORRECT  + i, correctAnswers.get(i));
            selectedAnswers.set(i, sel);
            correctAnswers .set(i, cor);
        }
        int savedIndex = sp.getInt(KEY_CURRENT_INDEX, 0);
        pager.post(() -> pager.setCurrentItem(savedIndex, false));
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle out) {
        super.onSaveInstanceState(out);
        out.putInt("currentPosition", pager.getCurrentItem());
        out.putStringArrayList("selectedAnswers", selectedAnswers);
        out.putStringArrayList("correctAnswers",  correctAnswers);
    }


    /**
     * Ensures the database table for questions is populated.
     * If empty, it loads data from assets/state_capitals.csv (first line is a header).
     */
    private static void ensureQuestionsLoaded(Context ctx) {
        StateCapitalsDBHelper h = StateCapitalsDBHelper.getInstance(ctx);
        SQLiteDatabase db = h.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT COUNT(*) FROM " + StateCapitalsDBHelper.TABLE_STATECAPITALS, null);
        int count = 0;
        if (c.moveToFirst()) count = c.getInt(0);
        c.close();
        db.close();
        if (count > 0) return;

        try {
            SQLiteDatabase wdb = h.getWritableDatabase();
            InputStream is = ctx.getAssets().open("state_capitals.csv");
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String line; boolean header = true;
            while ((line = br.readLine()) != null) {
                if (header) { header = false; continue; }
                String[] p = line.split(",");
                if (p.length < 4) continue;

                ContentValues v = new ContentValues();
                v.put(StateCapitalsDBHelper.STATECAPITALS_COLUMN_STATE,   p[0].trim());
                v.put(StateCapitalsDBHelper.STATECAPITALS_COLUMN_CAPITAL, p[1].trim());
                v.put(StateCapitalsDBHelper.STATECAPITALS_COLUMN_OPTION1, p[2].trim());
                v.put(StateCapitalsDBHelper.STATECAPITALS_COLUMN_OPTION2, p[3].trim());
                wdb.insert(StateCapitalsDBHelper.TABLE_STATECAPITALS, null, v);
            }
            br.close(); wdb.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns a shuffled list of random row IDs for the quiz questions.
     *
     * @param ctx   application context
     * @param count number of random questions to select
     * @return list of randomly chosen IDs
     */
    private static ArrayList<Integer> getRandomQuestionIds(Context ctx, int count) {
        ArrayList<Integer> ids = new ArrayList<>();
        StateCapitalsDBHelper h = StateCapitalsDBHelper.getInstance(ctx);
        SQLiteDatabase db = h.getReadableDatabase();
        Cursor c = db.query(StateCapitalsDBHelper.TABLE_STATECAPITALS,
                new String[]{StateCapitalsDBHelper.STATECAPITALS_COLUMN_ID},
                null, null, null, null, null);
        while (c.moveToNext()) {
            ids.add(c.getInt(c.getColumnIndexOrThrow(StateCapitalsDBHelper.STATECAPITALS_COLUMN_ID)));
        }
        c.close(); db.close();
        Collections.shuffle(ids);
        if (ids.size() > count) {
            return new ArrayList<>(ids.subList(0, count));
        }
        return ids;
    }
}
