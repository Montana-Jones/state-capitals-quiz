package edu.uga.cs.statecapitals;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;

import java.text.SimpleDateFormat;
import java.util.Date;

import edu.uga.cs.statecapitals.StateCapitalsDBHelper;

public class QuizResultTask extends AsyncTask<Void, Void, Void> {

    private Context context;
    private int score;

    public QuizResultTask(Context context, int score) {
        this.context = context;
        this.score = score;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        StateCapitalsDBHelper dbHelper = StateCapitalsDBHelper.getInstance(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        String datetime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        values.put(StateCapitalsDBHelper.QUIZ_RESULTS_COLUMN_DATETIME, datetime);
        values.put(StateCapitalsDBHelper.QUIZ_RESULTS_COLUMN_SCORE, score);

        db.insert(StateCapitalsDBHelper.TABLE_QUIZ_RESULTS, null, values);
        db.close();

        return null;
    }
}
