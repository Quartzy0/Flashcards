package me.quartzy.flashcards.database;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Collection {
    @PrimaryKey
    public long uid;

    @ColumnInfo
    public String name;
    @ColumnInfo
    public int cards;
}
