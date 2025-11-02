package edu.uga.cs.statecapitals;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;

/**
 * Saves a quiz result asynchronously so the UI is never blocked.
 */
public class QuizResultTask extends AsyncTask<Void, Void, Boolean> {

    private final Context app;
    private final int score;
    private final int total;

    public QuizResultTask(Context ctx, int score, int total) {
        this.app = ctx.getApplicationContext();
        this.score = score;
        this.total = total;
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        try {
            StateCapitalsDBHelper h = StateCapitalsDBHelper.getInstance(app);
            SQLiteDatabase db = h.getWritableDatabase();
            ContentValues v = new ContentValues();
            v.put(StateCapitalsDBHelper.RESULTS_COLUMN_TAKEN_AT, System.currentTimeMillis());
            v.put(StateCapitalsDBHelper.RESULTS_COLUMN_SCORE, score);
            v.put(StateCapitalsDBHelper.RESULTS_COLUMN_TOTAL, total);
            db.insert(StateCapitalsDBHelper.TABLE_RESULTS, null, v);
            db.close();
            return true;
        } catch (Exception e) { e.printStackTrace(); return false; }
    }
}
