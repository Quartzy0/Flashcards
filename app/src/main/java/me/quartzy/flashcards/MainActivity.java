package me.quartzy.flashcards;

import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.room.Room;

import me.quartzy.flashcards.database.Card;
import me.quartzy.flashcards.database.CardsDao;
import me.quartzy.flashcards.database.CardsDatabase;
import me.quartzy.flashcards.database.Collection;
import me.quartzy.flashcards.databinding.ActivityMainBinding;

import android.view.Menu;
import android.view.MenuItem;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    public static CardsDatabase db;
    public static Random random;

    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = Room.databaseBuilder(getApplicationContext(), CardsDatabase.class, "cards-database").build();
        SharedPreferences preferences = getPreferences(MODE_PRIVATE);
        if (!preferences.getBoolean("databasePopulated", false)){
            SharedPreferences.Editor edit = preferences.edit();
            edit.putBoolean("databasePopulated", true);
            edit.apply();
            CardsDao cardsDao = db.cardsDao();
            try {
                BufferedInputStream stream = new BufferedInputStream(getAssets().open("cards-prepackaged.csv", AssetManager.ACCESS_BUFFER));
                BufferedReader br = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8));
                List<Card> cards = new ArrayList<>();
                String line;
                while ((line = br.readLine()) != null){
                    String[] split = line.split(",", 4);
                    Card card = new Card();
                    card.uid = Integer.parseInt(split[0]);
                    card.collection_uid = Integer.parseInt(split[1]);
                    card.value2 = split[2];
                    card.value1 = split[3];
                    cards.add(card);
                }
                br.close();
                cardsDao.insertCards(cards);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            try{
                BufferedInputStream stream = new BufferedInputStream(getAssets().open("collections-prepackaged.csv", AssetManager.ACCESS_BUFFER));
                BufferedReader br = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8));
                List<Collection> collections = new ArrayList<>();
                String line;
                while ((line = br.readLine()) != null){
                    String[] split = line.split(",", 3);
                    Collection collection = new Collection();
                    collection.uid = Integer.parseInt(split[0]);
                    collection.name = split[1];
                    collection.cards = Integer.parseInt(split[2]);
                    collections.add(collection);
                }
                br.close();
                cardsDao.insertCollections(collections);
            }catch (IOException e){
                throw new RuntimeException(e);
            }
        }
        random = new Random();

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }
}