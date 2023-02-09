package me.quartzy.flashcards;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.MoreExecutors;

import java.util.List;
import java.util.concurrent.ExecutionException;

import me.quartzy.flashcards.database.CardsDao;
import me.quartzy.flashcards.database.Collection;
import me.quartzy.flashcards.databinding.FragmentFirstBinding;

public class CollectionsFragment extends Fragment implements AddCollectionDialogFragment.AddCollectionDialogListener {

    private FragmentFirstBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentFirstBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.collectionsList.setAdapter(new CollectionsAdapter());

        binding.collectionsAdd.setOnClickListener(v -> {
            AddCollectionDialogFragment addCollectionDialogFragment = new AddCollectionDialogFragment(this);
            addCollectionDialogFragment.show(getChildFragmentManager(), AddCollectionDialogFragment.TAG);
        });

        CardsDao cardsDao = MainActivity.db.cardsDao();
        ListenableFuture<List<Collection>> allCollection = cardsDao.getAllCollection();
        allCollection.addListener(() -> {
            try {
                List<Collection> collections = allCollection.get();
                ((CollectionsAdapter) binding.collectionsList.getAdapter()).data = collections;
                ((CollectionsAdapter) binding.collectionsList.getAdapter()).notifyDataSetChanged();
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
    public void collectionAdded(DialogFragment dialog, Collection newCollection) {
        CollectionsAdapter adapter = (CollectionsAdapter) binding.collectionsList.getAdapter();
        adapter.data.add(newCollection);
        adapter.notifyDataSetChanged();
    }
}