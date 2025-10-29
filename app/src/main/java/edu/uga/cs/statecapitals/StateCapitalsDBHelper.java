package edu.uga.cs.statecapitals;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class StateCapitalsDBHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "statecapitals.db";
    private static final int DB_VERSION = 1;

    // State capitals table
    public static final String TABLE_STATECAPITALS = "statecapitals";
    public static final String STATECAPITALS_COLUMN_ID = "_id";
    public static final String STATECAPITALS_COLUMN_STATE = "state";
    public static final String STATECAPITALS_COLUMN_CAPITAL = "capital";
    public static final String STATECAPITALS_COLUMN_OPTION1 = "option1";
    public static final String STATECAPITALS_COLUMN_OPTION2 = "option2";

    private static final String CREATE_STATECAPITALS =
            "CREATE TABLE " + TABLE_STATECAPITALS + " (" +
                    STATECAPITALS_COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    STATECAPITALS_COLUMN_STATE + " TEXT, " +
                    STATECAPITALS_COLUMN_CAPITAL + " TEXT, " +
                    STATECAPITALS_COLUMN_OPTION1 + " TEXT, " +
                    STATECAPITALS_COLUMN_OPTION2 + " TEXT" +
                    ")";

    // Quiz results table
    public static final String TABLE_QUIZ_RESULTS = "quiz_results";
    public static final String QUIZ_RESULTS_COLUMN_ID = "_id";
    public static final String QUIZ_RESULTS_COLUMN_DATETIME = "datetime";
    public static final String QUIZ_RESULTS_COLUMN_SCORE = "score";

    private static final String CREATE_QUIZ_RESULTS =
            "CREATE TABLE " + TABLE_QUIZ_RESULTS + " (" +
                    QUIZ_RESULTS_COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    QUIZ_RESULTS_COLUMN_DATETIME + " TEXT, " +
                    QUIZ_RESULTS_COLUMN_SCORE + " INTEGER" +
                    ")";

    private static StateCapitalsDBHelper helperInstance;

    private StateCapitalsDBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    public static synchronized StateCapitalsDBHelper getInstance(Context context) {
        if (helperInstance == null) {
            helperInstance = new StateCapitalsDBHelper(context.getApplicationContext());
        }
        return helperInstance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_STATECAPITALS);
        db.execSQL(CREATE_QUIZ_RESULTS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_STATECAPITALS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_QUIZ_RESULTS);
        onCreate(db);
    }
}
