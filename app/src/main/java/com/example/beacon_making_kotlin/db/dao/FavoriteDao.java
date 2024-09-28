package com.example.beacon_making_kotlin.db.dao;

import com.example.beacon_making_kotlin.db.entity.Favorite;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import java.util.List;

@Dao
public interface FavoriteDao {
    @Insert
    void insertFavorite(Favorite favorite);

    @Query("SELECT * FROM favorite")
    List<Favorite> getAllFavorites();

}
