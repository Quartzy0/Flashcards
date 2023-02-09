package me.quartzy.flashcards.database;

import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.Ignore;

@Entity
public class Collection {
    @PrimaryKey
    public long uid;

    @ColumnInfo
    public String name;
    @ColumnInfo
    public int cards;
    @ColumnInfo
    @Nullable
    public String description;

    @Ignore
    @Nullable
    public String src;
}
