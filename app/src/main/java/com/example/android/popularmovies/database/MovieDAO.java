package com.example.android.popularmovies.database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.example.android.popularmovies.Models.Movie;

import java.util.List;

@Dao
public interface MovieDAO {
    @Query("SELECT * FROM movie")
    LiveData<List<Movie>> loadAllMovies();

    /**
     * OnConflictStrategy.IGNORE was used here instead of
     * OnConflictStrategy.REPLACE to avoid unnecessary insert queries
     * because we know movie names don't change and id given to each movie
     * won't also change or be updated
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void saveMovie(Movie movie);
}
