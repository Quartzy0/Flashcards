package me.quartzy.flashcards.database;

import androidx.room.AutoMigration;
import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {Card.class, Collection.class}, version = 2)
public abstract class CardsDatabase extends RoomDatabase {
    public abstract CardsDao cardsDao();
}
