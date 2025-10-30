package edu.uga.cs.statecapitals;

import android.content.Context;
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
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ViewPager2 pager;
    private int totalQuestions = 6;
    private List<String> selectedAnswers = new ArrayList<>();
    private List<String> correctAnswers = new ArrayList<>();
    private List<Integer> questionIds = new ArrayList<>();
    private StateCapitalsPagerAdapter adapter;
    private boolean resultCalculated = false; // prevents multiple recalculations

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        readCSV(this);
        questionIds = getRandomQuestionIds(this, totalQuestions);

        if (savedInstanceState != null) {
            selectedAnswers = savedInstanceState.getStringArrayList("selectedAnswers");
            correctAnswers = savedInstanceState.getStringArrayList("correctAnswers");
        } else {
            for (int i = 0; i < totalQuestions; i++) {
                selectedAnswers.add(null);
                correctAnswers.add(null);
            }
        }

        pager = findViewById(R.id.viewpager);
        adapter = new StateCapitalsPagerAdapter(
                getSupportFragmentManager(),
                getLifecycle(),
                questionIds
        );
        pager.setAdapter(adapter);

        pager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                // Result page is index = totalQuestions
                if (position == totalQuestions && !resultCalculated) {
                    resultCalculated = true;
                    int score = calculateScore();
                    new QuizResultTask(MainActivity.this, score).execute();
                    adapter.setScoreAndNotify(score);
                }
            }
        });

        if (savedInstanceState != null) {
            int restoredPosition = savedInstanceState.getInt("currentPosition", 0);
            pager.setCurrentItem(restoredPosition, false);
        }
    }

    public void saveAnswer(int questionIndex, String selected, String correct) {
        if (questionIndex < totalQuestions) {
            selectedAnswers.set(questionIndex, selected);
            correctAnswers.set(questionIndex, correct);
        }
    }

    private int calculateScore() {
        int score = 0;
        for (int i = 0; i < totalQuestions; i++) {
            if (selectedAnswers.get(i) != null && selectedAnswers.get(i).equals(correctAnswers.get(i))) {
                score++;
            }
        }
        return score;
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("currentPosition", pager.getCurrentItem());
        outState.putStringArrayList("selectedAnswers", new ArrayList<>(selectedAnswers));
        outState.putStringArrayList("correctAnswers", new ArrayList<>(correctAnswers));
    }

    public static void readCSV(Context context) {
        StateCapitalsDBHelper dbHelper = StateCapitalsDBHelper.getInstance(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(StateCapitalsDBHelper.TABLE_STATECAPITALS, null, null);

        try {
            InputStream is = context.getAssets().open("state_capitals.csv");
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String line;
            boolean firstLine = true;
            while ((line = reader.readLine()) != null) {
                if (firstLine) { firstLine = false; continue; }
                String[] parts = line.split(",");
                if (parts.length < 4) continue;

                String state = parts[0].trim();
                String capital = parts[1].trim();
                String option1 = parts[2].trim();
                String option2 = parts[3].trim();

                android.content.ContentValues values = new android.content.ContentValues();
                values.put(StateCapitalsDBHelper.STATECAPITALS_COLUMN_STATE, state);
                values.put(StateCapitalsDBHelper.STATECAPITALS_COLUMN_CAPITAL, capital);
                values.put(StateCapitalsDBHelper.STATECAPITALS_COLUMN_OPTION1, option1);
                values.put(StateCapitalsDBHelper.STATECAPITALS_COLUMN_OPTION2, option2);
                db.insert(StateCapitalsDBHelper.TABLE_STATECAPITALS, null, values);
            }
            reader.close();
            db.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static List<Integer> getRandomQuestionIds(Context context, int count) {
        List<Integer> ids = new ArrayList<>();
        StateCapitalsDBHelper dbHelper = StateCapitalsDBHelper.getInstance(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.query(StateCapitalsDBHelper.TABLE_STATECAPITALS,
                new String[]{StateCapitalsDBHelper.STATECAPITALS_COLUMN_ID},
                null, null, null, null, null);

        while (cursor.moveToNext()) {
            ids.add(cursor.getInt(cursor.getColumnIndexOrThrow(StateCapitalsDBHelper.STATECAPITALS_COLUMN_ID)));
        }

        cursor.close();
        db.close();

        Collections.shuffle(ids);
        if (ids.size() > count) ids = ids.subList(0, count);

        return ids;
    }
}
