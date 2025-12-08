package com.example.androidhabittracker;

import static androidx.core.app.ActivityCompat.recreate;
import static androidx.core.content.ContextCompat.getColor;
import static androidx.core.content.ContextCompat.getDrawable;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.widget.CompoundButtonCompat;

public class ProgressBarHabit extends Habit {
    public ProgressBarHabit(Context context, SQLiteDatabase db, int id) {
        super(context, db, id);
        initHabit();
    }

    @Override
    public void initHabit() {
        LinearLayout container = ((Activity)context).findViewById(R.id.container);
        LayoutInflater layoutInflater = LayoutInflater.from(context);

        View hContainer = layoutInflater.inflate(R.layout.progressbar_habit, container, false);
        GridLayout habitContainer = (GridLayout) hContainer;
        habitContainer.setId(View.generateViewId());

        TextView name = habitContainer.findViewById(R.id.CHName);
        name.setText(this.name);

        TextView desc = habitContainer.findViewById(R.id.CHDesc);
        desc.setText(this.desc);

        ImageView habitIV = habitContainer.findViewById(R.id.habitIV);
        habitIV.setColorFilter(getColor(context, color));
        habitIV.setImageDrawable(getDrawable(context, this.icon));

        CheckBox check = habitContainer.findViewById(R.id.CHCheck);
        check.setChecked(dbUtils.isHabitChecked(id));

        int states[][] = {{android.R.attr.state_checked}, {}};
        int colors[] = {getColor(context, color), getColor(context, color)};
        CompoundButtonCompat.setButtonTintList(check, new ColorStateList(states, colors));

        ProgressBar progressBar = habitContainer.findViewById(R.id.habitPB);
        progressBar.setMax(this.target);
        progressBar.getProgressDrawable().setTint(colors[0]);
        progressBar.setProgress(checkDates.length, true);

        check.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(@NonNull CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    dbUtils.checkHabit(id);
                } else {
                    dbUtils.uncheckHabit(id);
                }
                reloadHabit(context, habitContainer.getId(), isChecked);
            }
        });

        habitContainer.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.DefaultDialog);
                builder
                        .setTitle("Удаление")
                        .setMessage("Удалить привычку \'" + name.getText().toString() + "\'?")
                        .setPositiveButton("Да", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dbUtils.deleteHabit(id);
                                recreate((Activity) context);
                                dialog.cancel();
                            }
                        })
                        .setNegativeButton("Нет", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        }).create().show();
                return true;
            }
        });
        container.addView(habitContainer);
    }

    @Override
    public void reloadHabit(Context context, int id, boolean check) {
        LinearLayout container = ((Activity)context).findViewById(R.id.container);
        GridLayout habitContainer = container.findViewById(id);
        ProgressBar progressBar = habitContainer.findViewById(R.id.habitPB);
        if (check) {
            progressBar.setProgress(progressBar.getProgress() + 1, true);
        } else {
            progressBar.setProgress(progressBar.getProgress() - 1, true);
        }
        if (progressBar.getProgress() == target) {
            dbUtils.deleteHabit(this.id);
            recreate((Activity) context);
        }
    }
}
