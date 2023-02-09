package me.quartzy.flashcards;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;

import androidx.fragment.app.FragmentManager;
import androidx.navigation.Navigation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import me.quartzy.flashcards.database.Card;

public class CardsAdapter extends BaseAdapter {
    public List<Card> data;
    private AddFlashcardDialogFragment.AddFlashcardDialogListener listener;
    private FragmentManager fragmentManager;

    public CardsAdapter(AddFlashcardDialogFragment.AddFlashcardDialogListener listener, FragmentManager fragmentManager) {
        this.data = new ArrayList<>();
        this.listener = listener;
        this.fragmentManager = fragmentManager;
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
        Collections.sort(data, Comparator.comparing(o -> o.value1));
        super.notifyDataSetChanged();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Button button = new Button(parent.getContext());
        button.setText(data.get(position).value1);
        button.setOnClickListener(v -> {
            AddFlashcardDialogFragment addFlashcardDialogFragment = new AddFlashcardDialogFragment(this.listener, data.get(position));
            addFlashcardDialogFragment.show(this.fragmentManager, AddFlashcardDialogFragment.TAG);
        });
        return button;
    }
}
