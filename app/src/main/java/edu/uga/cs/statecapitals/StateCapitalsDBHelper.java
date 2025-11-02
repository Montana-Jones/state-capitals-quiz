package edu.uga.cs.statecapitals;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * SQLite helper with two tables:
 * - state_capitals: questions (state, capital, option1, option2)
 * - quiz_results:   past results (taken_at, score, total)
 */
public class StateCapitalsDBHelper extends SQLiteOpenHelper {

    public static final String DB_NAME = "state_capitals.db";
    public static final int DB_VERSION = 1;

    // Questions table
    public static final String TABLE_STATECAPITALS = "state_capitals";
    public static final String STATECAPITALS_COLUMN_ID = "_id";
    public static final String STATECAPITALS_COLUMN_STATE = "state";
    public static final String STATECAPITALS_COLUMN_CAPITAL = "capital";
    public static final String STATECAPITALS_COLUMN_OPTION1 = "option1";
    public static final String STATECAPITALS_COLUMN_OPTION2 = "option2";

    // Results table
    public static final String TABLE_RESULTS = "quiz_results";
    public static final String RESULTS_COLUMN_ID = "_id";
    public static final String RESULTS_COLUMN_TAKEN_AT = "taken_at";
    public static final String RESULTS_COLUMN_SCORE = "score";
    public static final String RESULTS_COLUMN_TOTAL = "total";

    private static volatile StateCapitalsDBHelper instance;

    /** Get singleton instance. */
    public static StateCapitalsDBHelper getInstance(Context ctx) {
        if (instance == null) {
            synchronized (StateCapitalsDBHelper.class) {
                if (instance == null) {
                    instance = new StateCapitalsDBHelper(ctx.getApplicationContext());
                }
            }
        }
        return instance;
    }

    private StateCapitalsDBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_STATECAPITALS + " (" +
                STATECAPITALS_COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                STATECAPITALS_COLUMN_STATE + " TEXT NOT NULL, " +
                STATECAPITALS_COLUMN_CAPITAL + " TEXT NOT NULL, " +
                STATECAPITALS_COLUMN_OPTION1 + " TEXT NOT NULL, " +
                STATECAPITALS_COLUMN_OPTION2 + " TEXT NOT NULL)");

        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_RESULTS + " (" +
                RESULTS_COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                RESULTS_COLUMN_TAKEN_AT + " INTEGER NOT NULL, " +
                RESULTS_COLUMN_SCORE + " INTEGER NOT NULL, " +
                RESULTS_COLUMN_TOTAL + " INTEGER NOT NULL)");
    }

    @Override public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Simple demo upgrade: drop and recreate
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_STATECAPITALS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RESULTS);
        onCreate(db);
    }
}
