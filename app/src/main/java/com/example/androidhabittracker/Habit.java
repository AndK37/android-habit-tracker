package com.example.androidhabittracker;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.Date;

public abstract class Habit {
    protected int id;
    protected String name, desc;
    protected Double target;
    protected Date startDate;
    protected String[] checkDates;

    public Habit(SQLiteDatabase db, int id) {
        DBUtils dbUtils = new DBUtils(db);
        Cursor cursor = dbUtils.getHabit(id);

        this.id = id;
        this.name = cursor.getString(0);
        this.desc = cursor.getString(1);
        this.target = cursor.getDouble(2);
//        TODO: habit colors and types
//        this.type =
//        this.color = cursor.getString(0);
//        this.startDate = cursor.getString(0);
    }
}
