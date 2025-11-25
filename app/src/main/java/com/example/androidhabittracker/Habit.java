package com.example.androidhabittracker;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.Date;

public abstract class Habit {
    protected int id, color;
    protected String name, desc;
    protected int target;
    protected Date startDate;
    protected String[] checkDates;
    protected DBUtils dbUtils;
    protected Cursor cursor;
    protected Context context;

    public Habit(Context context, SQLiteDatabase db, int id) {
        this.dbUtils = new DBUtils(db);
        this.cursor = dbUtils.getHabit(id);
        this.context = context;

        this.id = id;
        this.name = cursor.getString(0);
        this.desc = cursor.getString(1);
        this.target = cursor.getInt(2);
        this.color = getHabitColor(cursor.getString(4));
        cursor.close();

        this.checkDates = dbUtils.getHabitChecks(id).toArray(new String[0]);
    }

    public abstract void initHabit();
    public abstract void reloadHabit(Context context, int id, boolean check);

    protected int getHabitColor(String color) {
        switch(color) {
            case "PURPLE": return R.color.purple;
            case "PINK": return R.color.pink;
            case "RED": return R.color.red;
            case "ORANGE": return R.color.orange;
            case "YELLOW": return R.color.yellow;
            case "BLUE": return R.color.blue;
            case "GREEN": return R.color.green;
        }
        return -1;
    }
}
