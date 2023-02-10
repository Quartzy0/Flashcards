package me.quartzy.flashcards;

import android.os.Handler;
import android.os.Looper;

import com.google.common.util.concurrent.MoreExecutors;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import me.quartzy.flashcards.database.Card;
import me.quartzy.flashcards.database.CardsDao;
import me.quartzy.flashcards.database.Collection;

public class Importer {

    public static Handler handler;
    private static ExecutorService executor;

    public static Collection import_collection(BufferedReader br, CardsDao dao) throws IOException, ExecutionException, InterruptedException {
        String line = br.readLine();
        String[] split = line.split("\\|", 3);
        Collection collection = new Collection();
        Random random = new Random();
        collection.uid = random.nextLong();
        collection.name = split[0];
        collection.description = split[1];
        collection.cards = Integer.parseInt(split[2]);

        List<Card> cards = new ArrayList<>(collection.cards);
        for (int i = 0; i < collection.cards; i++){
            line = br.readLine();
            String[] sp = line.split("\\|", 2);
            if (split.length < 2) break;
            Card card = new Card();
            card.uid = random.nextLong();
            card.value2 = sp[0];
            card.value1 = sp[1];
            card.collection_uid = collection.uid;
            cards.add(card);
        }
        br.close();
        dao.insertCollection(collection).get();
        dao.insertCards(cards).get();
        return collection;
    }

    public static Collection import_collection(InputStream ios, CardsDao dao) throws IOException, ExecutionException, InterruptedException {
        return import_collection(new BufferedReader(new InputStreamReader(new BufferedInputStream(ios))), dao);
    }

    public interface OnCollectionProcessedListener {
        public void onProcessed(Collection result, Handler handler);
    }

    public static void import_collection(URL url, CardsDao dao, OnCollectionProcessedListener processedListener) throws IOException {
        if (executor == null)  executor = Executors.newSingleThreadExecutor();
        if (handler == null) handler = new Handler(Looper.getMainLooper());

        Runnable bgTask = () -> {
            try{
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setDoOutput(false);
                conn.setRequestMethod("GET");
                conn.connect();

                int status = conn.getResponseCode();
                if (status != HttpURLConnection.HTTP_OK){
                    System.out.printf("Error: " + status);
                    return;
                }

                processedListener.onProcessed(import_collection(conn.getInputStream(), dao), handler);
            }catch (IOException e){
                throw new RuntimeException(e);
            } catch (ExecutionException e) {
                throw new RuntimeException(e);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        };

        executor.execute(bgTask);
    }

    public static List<Collection> list_collections(BufferedReader br) throws IOException{
        String line;
        List<Collection> collections = new ArrayList<>();
        while ((line = br.readLine()) != null){
            String[] split = line.split("\\|", 4);
            if (split.length < 3) break;
            Collection newCollection = new Collection();
            newCollection.uid = -1;
            newCollection.name = split[0];
            newCollection.description = split[1];
            newCollection.cards = Integer.parseInt(split[2]);
            if (split.length == 4){
                newCollection.src = split[3];
            }
            collections.add(newCollection);
        }
        br.close();
        return collections;
    }

    public static List<Collection> list_collections(InputStream ios) throws IOException{
        return list_collections(new BufferedReader(new InputStreamReader(new BufferedInputStream(ios))));
    }

    public interface OnCollectionsProcessedListener {
        public void onProcessed(List<Collection> result, Handler handler);
    }

    public static void list_collections(URL url, OnCollectionsProcessedListener onProcessedListener){
        if (executor == null)  executor = Executors.newSingleThreadExecutor();
        if (handler == null) handler = new Handler(Looper.getMainLooper());

        Runnable bgTask = () -> {
            try {
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setDoOutput(false);
                conn.connect();

                int status = conn.getResponseCode();
                if (status != HttpURLConnection.HTTP_OK){
                    System.out.printf("Error: " + status);
                    return;
                }

                onProcessedListener.onProcessed(list_collections(conn.getInputStream()), handler);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        };
        executor.execute(bgTask);
    }
}
