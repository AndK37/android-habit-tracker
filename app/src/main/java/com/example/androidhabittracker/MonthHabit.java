package com.example.androidhabittracker;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

public class MonthHabit extends Habit {
    public MonthHabit(Context context, SQLiteDatabase db, int id) {
        super(context, db, id);
        initHabit();
    }

    @Override
    public void initHabit() {

    }

    @Override
    public void reloadHabit(Context context, int id, boolean check) {

    }
}
