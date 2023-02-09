package me.quartzy.flashcards;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import me.quartzy.flashcards.database.Collection;

public class ImportCollectionsAdapter extends RecyclerView.Adapter<ImportCollectionsAdapter.ImportCollectionsViewHolder>{
    public List<Collection> data;
    private ImportCollectionDialogFragment parent;

    public ImportCollectionsAdapter(List<Collection> data, ImportCollectionDialogFragment parent) {
        this.data = data;
        this.parent = parent;
        sort();
    }

    @NonNull
    @Override
    public ImportCollectionsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_import_collection, parent, false);
        return new ImportCollectionsViewHolder(view);
    }

    public void sort(){
        Collections.sort(data, Comparator.comparing(collection -> collection.name));
    }

    @Override
    public void onBindViewHolder(@NonNull ImportCollectionsViewHolder holder, int position) {
        holder.bindData(data.get(position), parent);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class ImportCollectionsViewHolder extends RecyclerView.ViewHolder {
        public TextView collectionTitle;
        public TextView collectionDescription;
        public TextView collectionCardCount;
        public Button importButton;
        public ProgressBar loadingBar;


        public ImportCollectionsViewHolder(@NonNull View itemView) {
            super(itemView);
            collectionTitle = itemView.findViewById(R.id.collection_import_title);
            collectionDescription = itemView.findViewById(R.id.collection_import_description);
            collectionCardCount = itemView.findViewById(R.id.collection_import_card_count);
            importButton = itemView.findViewById(R.id.collection_import_button);
            loadingBar = itemView.findViewById(R.id.import_collection_loading);
        }

        public void bindData(Collection collection, ImportCollectionDialogFragment parent){
            loadingBar.setVisibility(View.GONE);
            collectionTitle.setText(collection.name);
            collectionDescription.setText(collection.description);
            collectionCardCount.setText(collection.cards + "");
            importButton.setOnClickListener(v -> {
                loadingBar.setVisibility(View.VISIBLE);
                try {
                    Importer.import_collection(new URL(collection.src), MainActivity.db.cardsDao(), (result, handler) -> {
                        handler.post(() -> {
                            Toast.makeText(parent.getContext(), "Collection has been imported", Toast.LENGTH_SHORT).show();
                            parent.dismiss();
                            parent.listener.collectionImported(parent, result);
                        });
                    });
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        }
    }
}
