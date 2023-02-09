package me.quartzy.flashcards;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import me.quartzy.flashcards.database.CardsDao;
import me.quartzy.flashcards.database.Collection;
import me.quartzy.flashcards.databinding.DialogAddCollectionBinding;

public class AddCollectionDialogFragment extends DialogFragment {

    public static final String TAG = "AddCollectionDialogFragment";

    public interface AddCollectionDialogListener{
        public void collectionAdded(DialogFragment dialog, Collection collection);
    }
    AddCollectionDialogListener listener;
    public DialogAddCollectionBinding binding;

    public AddCollectionDialogFragment(AddCollectionDialogListener listener) {
        super();
        this.listener = listener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();

        binding = DialogAddCollectionBinding.inflate(inflater);
        builder.setView(binding.getRoot())
                .setPositiveButton("Add", (dialog, which) -> {
                    Collection newCollection = new Collection();
                    newCollection.name = binding.collectionNameInput.getText().toString();
                    newCollection.cards = 0;
                    newCollection.uid = MainActivity.random.nextLong();

                    CardsDao cardsDao = MainActivity.db.cardsDao();
                    cardsDao.insertCollection(newCollection);
                    if (listener != null) listener.collectionAdded(this, newCollection);

                }).setNegativeButton("Cancel", (dialog, which) -> {
                    AddCollectionDialogFragment.this.getDialog().cancel();
                });
        return builder.create();
    }
}
