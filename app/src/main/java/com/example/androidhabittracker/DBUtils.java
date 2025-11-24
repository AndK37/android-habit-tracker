package com.example.androidhabittracker;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

public class DBUtils {
    private SQLiteDatabase db;
    public DBUtils(SQLiteDatabase db) {
        this.db = db;
    }
    public Cursor getHabit(int id) {
        Cursor cursor = db.rawQuery(
                "SELECT h.name, h.desc, h.target, t.name, c.name, h.start_date FROM habits h\n" +
                "JOIN types t ON h.type_id = t.id \n" +
                "JOIN colors c on h.color_id = c.id\n" +
                "WHERE h.id = ?", new String[]{Integer.toString(id)});
        cursor.moveToFirst();
        return cursor;
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
    public Cursor getWeekHabitChecks(int id) {
        Cursor cursor = db.rawQuery(
                "SELECT check_date FROM habits\n" +
                        "JOIN history ON habits.id = history.habit_id\n" +
                        "WHERE habits.id = ?\n" +
                        "AND history.check_date BETWEEN \n" +
                        "strftime('%Y-%m-%d', 'now', '-6 days')\n" +
                        "AND\n" +
                        "DATE('now')\n" +
                        "ORDER BY check_date",
                new String[] {Integer.toString(id)});
        cursor.moveToFirst();
        return cursor;
    }

    public ArrayList<String> getHabitTypes() {
        Cursor cursor = db.rawQuery("SELECT name FROM types", null);
        ArrayList<String> types = new ArrayList<>();

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            types.add(cursor.getString(0));
            cursor.moveToNext();
        }
        return types;
    }

    public Integer getHabitTypeIdByName(String name) {
        Cursor cursor = db.rawQuery("SELECT id FROM types WHERE name = ?", new String[] {name});
        cursor.moveToFirst();
        return cursor.getInt(0);
    }

    public ArrayList<String> getHabitColors() {
        Cursor cursor = db.rawQuery("SELECT name FROM colors", null);
        ArrayList<String> types = new ArrayList<>();

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            types.add(cursor.getString(0));
            cursor.moveToNext();
        }
        return types;
    }

    public Integer getHabitColorIdByName(String name) {
        Cursor cursor = db.rawQuery("SELECT id FROM colors WHERE name = ?", new String[] {name});
        cursor.moveToFirst();
        return cursor.getInt(0);
    }

    public void addHabit(String name, String desc, String type, String color, Integer target) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("name", name);
        contentValues.put("desc", desc);
        contentValues.put("type_id", getHabitTypeIdByName(type));
        contentValues.put("color_id", getHabitColorIdByName(color));
        contentValues.put("target", target);
        db.insert("habits", null, contentValues);
    }

}
