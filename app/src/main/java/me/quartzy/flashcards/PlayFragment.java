package me.quartzy.flashcards;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.graphics.Path;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import org.checkerframework.checker.units.qual.A;

import me.quartzy.flashcards.database.Card;
import me.quartzy.flashcards.databinding.FragmentPlayBinding;
public class PlayFragment extends Fragment {

    private FragmentPlayBinding binding;
    private int cardId;
    private Card[] cards;
    private boolean val2;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentPlayBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        cards = PlayFragmentArgs.fromBundle(getArguments()).getCards();
        cardId = MainActivity.random.nextInt(cards.length);
        binding.card.setText(cards[cardId].value1);
        val2 = false;

        binding.card.setOnClickListener(v -> {
            if (val2){
                cardId = MainActivity.random.nextInt(cards.length);
                val2 = false;


                ObjectAnimator left = ObjectAnimator.ofFloat(binding.card, "translationX", 0, -1000f);
                left.setDuration(200);
                ObjectAnimator right = ObjectAnimator.ofFloat(binding.card, "translationX", -1000f, 1000f);
                right.setDuration(0);
                right.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        binding.card.setText(cards[cardId].value1);
                    }
                });
                ObjectAnimator left2 = ObjectAnimator.ofFloat(binding.card, "translationX", 1000f, 0);
                left2.setDuration(200);
                AnimatorSet animation = new AnimatorSet();
                animation.play(left).before(right).before(left2);
                animation.start();
            }else{
                ObjectAnimator p1 = ObjectAnimator.ofFloat(binding.card, "rotationY", 0f, -90f);
                p1.setDuration(150);
                ObjectAnimator p2 = ObjectAnimator.ofFloat(binding.card, "rotationY", -90f, 90f);
                p2.setDuration(0);
                p2.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        binding.card.setText(cards[cardId].value2);
                    }
                });
                ObjectAnimator p3 = ObjectAnimator.ofFloat(binding.card, "rotationY", 90f, 0f);
                p3.setDuration(150);
                AnimatorSet animation = new AnimatorSet();
                animation.play(p1).before(p2).before(p3);
                animation.start();


                val2 = true;
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}