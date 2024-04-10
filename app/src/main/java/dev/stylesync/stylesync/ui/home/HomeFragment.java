package dev.stylesync.stylesync.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import java.util.ArrayList;

import dev.stylesync.stylesync.R;
import dev.stylesync.stylesync.databinding.FragmentHomeBinding;
import dev.stylesync.stylesync.ui.home.viewpager.ViewPagerAdapter;
import dev.stylesync.stylesync.ui.home.viewpager.ViewPagerItem;
import dev.stylesync.stylesync.ui.viewmodel.SharedViewModel;

public class HomeFragment extends Fragment {

    ViewPager2 viewPager2;
    private FragmentHomeBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        viewPager2 = root.findViewById(R.id.view_pager);

        SharedViewModel sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        sharedViewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            if (isLoading) {
                viewPager2.setVisibility(View.GONE);
            } else {
                sharedViewModel.getViewPagerItems().observe(getViewLifecycleOwner(), items -> {
                    if(items != null && !items.isEmpty()) {
                        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(items);
                        viewPager2.setAdapter(viewPagerAdapter);
                        viewPager2.setVisibility(View.VISIBLE);
                    } else {
                        viewPager2.setVisibility(View.GONE);
                    }
                });
            }
        });

        viewPager2.setClipToPadding(false);
        viewPager2.setClipChildren(false);
        viewPager2.setOffscreenPageLimit(2);
        viewPager2.getChildAt(0).setOverScrollMode(View.OVER_SCROLL_NEVER);

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}