package com.example.android.popularmovies.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import com.example.android.popularmovies.Models.Movie;

@Database(entities = {Movie.class}, exportSchema = false, version = 1)
abstract public class AppDatabase extends RoomDatabase {
    private static final String DB_NAME = "popularmovies";
    private static AppDatabase instance;
    private final static Object LOCK = new Object();

    public static AppDatabase getInstance(Context context) {
        if (instance == null) {
            synchronized (LOCK) {
                instance = Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, DB_NAME)
                        .build();
            }
        }

        return instance;
    }

    public abstract MovieDAO movieDAO();
}
