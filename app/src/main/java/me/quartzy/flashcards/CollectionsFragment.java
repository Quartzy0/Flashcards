package me.quartzy.flashcards;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.MoreExecutors;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import me.quartzy.flashcards.database.CardsDao;
import me.quartzy.flashcards.database.Collection;
import me.quartzy.flashcards.databinding.FragmentFirstBinding;

public class CollectionsFragment extends Fragment implements AddCollectionDialogFragment.AddCollectionDialogListener, ImportCollectionDialogFragment.ImportCollectionDialogListener {

    private FragmentFirstBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentFirstBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.collectionsList.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.collectionsList.setAdapter(new CollectionsAdapter(new ArrayList<>(), this));

        binding.collectionsAdd.setOnClickListener(v -> {
            AddCollectionDialogFragment addCollectionDialogFragment = new AddCollectionDialogFragment(this, null);
            addCollectionDialogFragment.show(getChildFragmentManager(), AddCollectionDialogFragment.TAG);
        });

        binding.collectionsImport.setOnClickListener(v -> {
            ImportCollectionDialogFragment importCollectionDialogFragment = new ImportCollectionDialogFragment(this);
            importCollectionDialogFragment.show(getChildFragmentManager(), AddCollectionDialogFragment.TAG);
        });

        CardsDao cardsDao = MainActivity.db.cardsDao();
        ListenableFuture<List<Collection>> allCollection = cardsDao.getAllCollection();
        allCollection.addListener(() -> Importer.handler.post(() -> {
            try {
                List<Collection> collections = allCollection.get();
                ((CollectionsAdapter) binding.collectionsList.getAdapter()).data = collections;
                notifyDataChanged();
            } catch (ExecutionException e) {
                throw new RuntimeException(e);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }), MoreExecutors.directExecutor());
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
        notifyDataChanged();
    }

    @Override
    public void collectionChanged(DialogFragment dialogFragment, Collection collection) {
        CollectionsAdapter adapter = (CollectionsAdapter) binding.collectionsList.getAdapter();
        for (int i = 0; i < adapter.data.size(); i++) {
            if (adapter.data.get(i).uid==collection.uid){
                adapter.data.set(i, collection);
                break;
            }
        }
        notifyDataChanged();
    }

    @Override
    public void collectionImported(DialogFragment dialog, Collection collection) {
        CollectionsAdapter adapter = (CollectionsAdapter) binding.collectionsList.getAdapter();
        adapter.data.add(collection);
        notifyDataChanged();
    }

    public void notifyDataChanged(){
        CollectionsAdapter adapter = (CollectionsAdapter) binding.collectionsList.getAdapter();
        adapter.sortData();
        adapter.notifyDataSetChanged();
        if (adapter.data.size() == 0){
            binding.suggestionArrow.setVisibility(View.VISIBLE);
            binding.suggestionText.setVisibility(View.VISIBLE);
        }else{
            binding.suggestionArrow.setVisibility(View.GONE);
            binding.suggestionText.setVisibility(View.GONE);
        }
    }
}