package com.neu.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;

/**
 * Created by zhang on 2016/9/1.
 */
public class HistoryHelper extends SQLiteOpenHelper {
    private static final String DATABASENAME = "history.db";
    private static final int VERSION = 1;
    public HistoryHelper(Context context){
        super(context,DATABASENAME,null, VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "create table history (id integer primary key autoincrement,rec text)";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
