package dev.stylesync.stylesync.ui.wardrobe;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import dev.stylesync.stylesync.R;

public class WardrobeFragment extends Fragment {

    private WardrobeViewModel mViewModel;

    public static WardrobeFragment newInstance() {
        return new WardrobeFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_wardrobe, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(WardrobeViewModel.class);
        // TODO: Use the ViewModel
    }

}