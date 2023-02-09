package me.quartzy.flashcards;

import android.app.AlertDialog;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;

import androidx.navigation.Navigation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import me.quartzy.flashcards.database.CardsDao;
import me.quartzy.flashcards.database.Collection;

public class CollectionsAdapter extends BaseAdapter {
    public List<Collection> data;

    public CollectionsAdapter() {
        this.data = new ArrayList<>();
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public void notifyDataSetChanged() {
        Collections.sort(data, Comparator.comparing(o -> o.name));
        super.notifyDataSetChanged();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Button button = new Button(parent.getContext());
        button.setText(data.get(position).name + "(" + data.get(position).cards + ")");
        button.setOnClickListener(v -> {
            CollectionsFragmentDirections.ActionFirstFragmentToSecondFragment action =
                    CollectionsFragmentDirections.actionFirstFragmentToSecondFragment(data.get(position).uid);
            Navigation.findNavController(parent).navigate(action);
        });
        button.setOnLongClickListener(v -> {
            new AlertDialog.Builder(parent.getContext())
                    .setTitle("Delete collection")
                    .setMessage("Do you really want to delete collection '" + data.get(position).name + "' and all it's cards? This action cannot be reversed.")
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setPositiveButton(android.R.string.yes, (dialog, whichButton) -> {
                        CardsDao cardsDao = MainActivity.db.cardsDao();
                        cardsDao.deleteCollection(data.get(position));
                        cardsDao.deleteCollectionCards(data.get(position).uid);
                        data.remove(position);
                        notifyDataSetChanged();
                    })
                    .setNegativeButton(android.R.string.no, null).show();
            return false;
        });
        return button;
    }
}
