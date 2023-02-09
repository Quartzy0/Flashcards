package me.quartzy.flashcards;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import me.quartzy.flashcards.database.Collection;
import me.quartzy.flashcards.databinding.DialogImportCollectionBinding;

public class ImportCollectionDialogFragment extends DialogFragment {
    public static final String TAG = "ImportCollectionDialogFragment";

    public interface ImportCollectionDialogListener{
        public void collectionImported(DialogFragment dialog, Collection collection);
    }

    ImportCollectionDialogListener listener;
    public DialogImportCollectionBinding binding;

    public ImportCollectionDialogFragment(ImportCollectionDialogListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();

        binding = DialogImportCollectionBinding.inflate(inflater);
        builder.setView(binding.getRoot()).setNegativeButton("Cancel", (dialog, which) -> {
            ImportCollectionDialogFragment.this.getDialog().cancel();
        });
        binding.importCollectionsRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.importCollectionsRecycler.setAdapter(new ImportCollectionsAdapter(new ArrayList<>(), this));
        try {
            Importer.list_collections(new URL("https://quartzy.me/flashcards/index"), (result, handler) -> {
                handler.post(() -> {
                    ImportCollectionsAdapter adapter = (ImportCollectionsAdapter) binding.importCollectionsRecycler.getAdapter();
                    adapter.data = result;
                    adapter.sort();
                    adapter.notifyDataSetChanged();
                    binding.importLoadingCollections.setVisibility(View.GONE);
                });
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return builder.create();
    }
}
