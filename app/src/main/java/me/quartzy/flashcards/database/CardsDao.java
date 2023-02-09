package me.quartzy.flashcards.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.google.common.util.concurrent.ListenableFuture;

import java.util.List;

@Dao
public interface CardsDao {

    @Query("SELECT * FROM card WHERE collection_uid = :collection_uid")
    ListenableFuture<List<Card>> getCardsByCollection(long collection_uid);

    @Query("SELECT * FROM collection")
    ListenableFuture<List<Collection>> getAllCollection();

    @Insert
    ListenableFuture<Long> insertCollection(Collection collections);

    @Insert
    ListenableFuture<List<Long>> insertCollections(List<Collection> collections);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    ListenableFuture<Long> insertCard(Card cards);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    ListenableFuture<List<Long>> insertCards(List<Card> cards);

    @Delete
    ListenableFuture<Integer> deleteCard(Card card);

    @Delete
    ListenableFuture<Integer> deleteCollection(Collection card);

    @Query("DELETE FROM card WHERE collection_uid = :collection_uid")
    ListenableFuture<Integer> deleteCollectionCards(long collection_uid);

    @Query("UPDATE collection SET cards = cards + 1 WHERE uid = :collection_uid")
    ListenableFuture<Integer> incrementCardCount(long collection_uid);

    @Query("UPDATE collection SET cards = cards - 1 WHERE uid = :collection_uid")
    ListenableFuture<Integer> decrementCardCount(long collection_uid);
}
