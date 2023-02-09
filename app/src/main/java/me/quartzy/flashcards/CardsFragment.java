package me.quartzy.flashcards;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.MoreExecutors;

import java.util.List;
import java.util.concurrent.ExecutionException;

import me.quartzy.flashcards.CardsFragmentDirections;
import me.quartzy.flashcards.database.Card;
import me.quartzy.flashcards.database.CardsDao;
import me.quartzy.flashcards.databinding.FragmentSecondBinding;

public class CardsFragment extends Fragment implements AddFlashcardDialogFragment.AddFlashcardDialogListener {

    private FragmentSecondBinding binding;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentSecondBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.flashcardsList.setAdapter(new CardsAdapter(this, getChildFragmentManager()));

        long collection_uid = CardsFragmentArgs.fromBundle(getArguments()).getCollection();
        binding.flashcardsAdd.setOnClickListener(v -> {
            Card card = new Card();
            card.collection_uid = collection_uid;
            card.uid = -1;
            AddFlashcardDialogFragment addFlashcardDialogFragment = new AddFlashcardDialogFragment(this, card);
            addFlashcardDialogFragment.show(getChildFragmentManager(), AddFlashcardDialogFragment.TAG);
        });

        binding.flashcardsPlay.setOnClickListener(v -> {
            List<Card> data = ((CardsAdapter) binding.flashcardsList.getAdapter()).data;
            if (data.size() == 0){
                Toast.makeText(getContext(), "Add some flashcards first", Toast.LENGTH_SHORT).show();
                return;
            }
            Card[] cards = data.toArray(new Card[data.size()]);
            CardsFragmentDirections.ActionSecondFragmentToPlayFragment action =
                    CardsFragmentDirections.actionSecondFragmentToPlayFragment(cards);
            Navigation.findNavController(view).navigate(action);
        });

        CardsDao cardsDao = MainActivity.db.cardsDao();
        ListenableFuture<List<Card>> cardsByCollection = cardsDao.getCardsByCollection(collection_uid);
        cardsByCollection.addListener(() -> {
            try {
                ((CardsAdapter)binding.flashcardsList.getAdapter()).data = cardsByCollection.get();
                ((CardsAdapter)binding.flashcardsList.getAdapter()).notifyDataSetChanged();
            } catch (ExecutionException e) {
                throw new RuntimeException(e);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }, MoreExecutors.directExecutor());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void flashcardAdded(DialogFragment dialog, Card card) {
        ((CardsAdapter)binding.flashcardsList.getAdapter()).data.add(card);
        ((CardsAdapter)binding.flashcardsList.getAdapter()).notifyDataSetChanged();
        CardsDao cardsDao = MainActivity.db.cardsDao();
        cardsDao.incrementCardCount(card.collection_uid);
    }

    @Override
    public void flashcardRemoved(DialogFragment dialog, Card card) {
        ((CardsAdapter)binding.flashcardsList.getAdapter()).data.remove(card);
        ((CardsAdapter)binding.flashcardsList.getAdapter()).notifyDataSetChanged();
        CardsDao cardsDao = MainActivity.db.cardsDao();
        cardsDao.decrementCardCount(card.collection_uid);
    }
}