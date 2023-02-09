package me.quartzy.flashcards.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {Card.class, Collection.class}, version = 1)
public abstract class CardsDatabase extends RoomDatabase {
    public abstract CardsDao cardsDao();
}
