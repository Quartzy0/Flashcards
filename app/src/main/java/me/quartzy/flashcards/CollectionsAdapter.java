package me.quartzy.flashcards;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import me.quartzy.flashcards.database.CardsDao;
import me.quartzy.flashcards.database.Collection;

public class CollectionsAdapter extends RecyclerView.Adapter<CollectionsAdapter.CollectionsViewHolder> {
    public List<Collection> data;
    private CollectionsFragment parent;

    public CollectionsAdapter(List<Collection> data, CollectionsFragment parent) {
        this.data = data;
        this.parent = parent;
    }

    @NonNull
    @Override
    public CollectionsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_collection, parent, false);
        return new CollectionsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CollectionsViewHolder holder, int position) {
        holder.bindData(data.get(position), parent);
    }

    public void sortData(){
        Collections.sort(data, Comparator.comparing(collection -> collection.name));
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class CollectionsViewHolder extends RecyclerView.ViewHolder {
        public TextView collection_name;
        public TextView collection_description;
        public TextView card_count;
        public Button delete_collection;
        public CardView cardView;
        public ImageButton editButton;

        public CollectionsViewHolder(@NonNull View itemView) {
            super(itemView);
            collection_name = itemView.findViewById(R.id.collection_name);
            collection_description = itemView.findViewById(R.id.collection_description);
            card_count = itemView.findViewById(R.id.card_count);
            delete_collection = itemView.findViewById(R.id.delete_button);
            cardView = itemView.findViewById(R.id.card_view);
            editButton = itemView.findViewById(R.id.edit_button);
        }

        public void bindData(Collection collection, CollectionsFragment parent) {
            collection_name.setText(collection.name);
            if (collection.description != null)
                collection_description.setText(collection.description);
            else collection_description.setText("No description");
            card_count.setText(collection.cards + "");
            delete_collection.setOnClickListener(v -> {
                new AlertDialog.Builder(parent.getContext())
                        .setTitle("Delete collection")
                        .setMessage("Do you really want to delete collection '" + collection.name + "' and all it's cards? This action cannot be reversed.")
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton(android.R.string.yes, (dialog, whichButton) -> {
                            CardsDao cardsDao = MainActivity.db.cardsDao();
                            cardsDao.deleteCollection(collection);
                            cardsDao.deleteCollectionCards(collection.uid);
                            data.remove(collection);
                            parent.notifyDataChanged();
                        })
                        .setNegativeButton(android.R.string.no, null).show();
            });
            cardView.setOnClickListener(v -> {
                CollectionsFragmentDirections.ActionFirstFragmentToSecondFragment action =
                        CollectionsFragmentDirections.actionFirstFragmentToSecondFragment(collection.uid);
                Navigation.findNavController(parent.getView()).navigate(action);
            });
            editButton.setOnClickListener(v -> {
                AddCollectionDialogFragment addCollectionDialogFragment = new AddCollectionDialogFragment(parent, collection);
                addCollectionDialogFragment.show(parent.getChildFragmentManager(), AddCollectionDialogFragment.TAG);
            });
        }
    }
}
