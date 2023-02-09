package me.quartzy.flashcards;

import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.room.Room;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

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

    static final Migration MIGRATION_1_2 = new Migration(1,2) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE collection ADD COLUMN description TEXT");
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        db = Room.databaseBuilder(getApplicationContext(), CardsDatabase.class, "cards-database").addMigrations(MIGRATION_1_2).build();


        SharedPreferences preferences = getPreferences(MODE_PRIVATE);
        if (!preferences.getBoolean("databasePopulated", false)){
            SharedPreferences.Editor edit = preferences.edit();
            edit.putBoolean("databasePopulated", true);
            edit.apply();
            CardsDao cardsDao = db.cardsDao();
            try {
                Importer.import_collection(getAssets().open("french-basic.csv", AssetManager.ACCESS_BUFFER), cardsDao);
                Importer.import_collection(getAssets().open("french-basic1.csv", AssetManager.ACCESS_BUFFER), cardsDao);
            } catch (IOException e) {
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