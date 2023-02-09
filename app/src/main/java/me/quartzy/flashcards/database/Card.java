package me.quartzy.flashcards.database;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity
public class Card implements Parcelable {
    @PrimaryKey
    public long uid;

    @ColumnInfo
    public long collection_uid;
    @ColumnInfo
    public String value1;
    @ColumnInfo
    public String value2;

    protected Card(Parcel in) {
        uid = in.readLong();
        collection_uid = in.readLong();
        value1 = in.readString();
        value2 = in.readString();
    }

    public Card() {
    }

    public static final Creator<Card> CREATOR = new Creator<Card>() {
        @Override
        public Card createFromParcel(Parcel in) {
            return new Card(in);
        }

        @Override
        public Card[] newArray(int size) {
            return new Card[size];
        }
    };

    @Override
    public int describeContents() {
        return (int) uid;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeLong(uid);
        dest.writeLong(collection_uid);
        dest.writeString(value1);
        dest.writeString(value2);
    }
}
