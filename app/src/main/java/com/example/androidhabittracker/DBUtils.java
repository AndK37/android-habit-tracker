package com.example.androidhabittracker;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class DBUtils {
    private SQLiteDatabase db;
    public DBUtils(SQLiteDatabase db) {
        this.db = db;
    }
    public Cursor getAllHabits() {
        Cursor cursor = db.rawQuery(
                "SELECT h.id, h.name, h.desc, t.name, c.name FROM habits h\n" +
                "JOIN types t ON h.type_id = t.id \n" +
                "JOIN colors c on h.color_id = c.id\n" +
                "WHERE h.is_active = 1", null);
        cursor.moveToFirst();
        return cursor;
    }
    public boolean isHabitChecked(int id) {
        Cursor cursor = db.rawQuery(
                "SELECT * FROM history h \n" +
                "WHERE h.habit_id = ? \n" +
                "AND h.check_date = DATE('now')", new String[] { Integer.toString(id) });
        if (cursor.getCount() > 0) {
            return true;
        }
        return false;
    }
    public void checkHabit(int id) {
        if (!isHabitChecked(id)) {
            ContentValues contentValues = new ContentValues();
            contentValues.put("habit_id", id);
            db.insert("history", null, contentValues);
        }
    }
    public void uncheckHabit(int id) {
        if (isHabitChecked(id)) {
            db.delete("history", "habit_id = ? AND check_date = DATE('now')", new String[] {Integer.toString(id)});
        }
    }
    public Cursor getHabitChecks(int id) {
        Cursor cursor = db.rawQuery(
                "SELECT check_date FROM habits\n" +
                        "JOIN history ON habits.id = history.habit_id\n" +
                        "WHERE habits.id = ?\n" +
                        "AND strftime('%W', history.check_date, 'localtime', 'weekday 0', '-6 days')\n" +
                        "IS strftime('%W', 'now', 'localtime', 'weekday 0', '-6 days')\n" +
                        "ORDER BY check_date ",
                new String[] {Integer.toString(id)});
        cursor.moveToFirst();
        return cursor;
    }

}
