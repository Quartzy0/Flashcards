package me.quartzy.flashcards;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import me.quartzy.flashcards.database.Card;
import me.quartzy.flashcards.database.CardsDao;
import me.quartzy.flashcards.databinding.DialogAddFlashcardBinding;

public class AddFlashcardDialogFragment extends DialogFragment {
    public static final String TAG = "AddFlashcardDialogFragment";

    public interface AddFlashcardDialogListener{
        public void flashcardAdded(DialogFragment dialog, Card card);
        public void flashcardRemoved(DialogFragment dialog, Card card);
        public void flashcardEdited(DialogFragment dialog, Card card);
    }

    AddFlashcardDialogListener listener;
    Card card;
    public DialogAddFlashcardBinding binding;

    public AddFlashcardDialogFragment(AddFlashcardDialogListener listener, Card card) {
        super();
        this.listener = listener;
        this.card = card;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();

        binding = DialogAddFlashcardBinding.inflate(inflater);
        if (card.value1 != null) binding.input1.setText(card.value1);
        if (card.value2 != null) binding.input2.setText(card.value2);
        builder.setView(binding.getRoot()).setPositiveButton("Save", (dialog, which) -> {
            CardsDao cardsDao = MainActivity.db.cardsDao();
            card.value1 = binding.input1.getText().toString();
            card.value2 = binding.input2.getText().toString();
            if (card.uid == -1){
                card.uid = MainActivity.random.nextLong();
                if (listener != null) listener.flashcardAdded(this, card);
            }else{
                if (listener != null) listener.flashcardEdited(this, card);
            }
            cardsDao.insertCard(card);
        }).setNeutralButton("Cancel", (dialog, which) -> {

        });
        if (card.uid != -1){
            builder.setNegativeButton("Delete", (dialog, which) -> {
                CardsDao cardsDao = MainActivity.db.cardsDao();
                cardsDao.deleteCard(card);
                if (listener != null) listener.flashcardRemoved(this, card);
            });
        }

        return builder.create();
    }
}
