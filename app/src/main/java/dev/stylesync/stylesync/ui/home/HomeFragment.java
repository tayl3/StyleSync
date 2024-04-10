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

public class HomeFragment extends Fragment {

    ViewPager2 viewPager2;
    ArrayList<ViewPagerItem> itemList;

    private FragmentHomeBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        viewPager2 = root.findViewById(R.id.view_pager);
        int[] images = {R.drawable.baseline_10k_24, R.drawable.baseline_settings_24, R.drawable.ic_home_black_24dp};
        String[] headings = {"10k", "Settings", "Home"};
        String[] descriptions = {"10k image", "Settings icon", "Home button icon"};

        itemList = new ArrayList<>();
        for(int i = 0; i < images.length; i++) {
            ViewPagerItem viewPagerItem = new ViewPagerItem(images[i], headings[i], descriptions[i]);
            itemList.add(viewPagerItem);
        }

        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(itemList);
        viewPager2.setAdapter(viewPagerAdapter);
        viewPager2.setClipToPadding(false);
        viewPager2.setClipChildren(false);
        viewPager2.setOffscreenPageLimit(2);
        viewPager2.getChildAt(0).setOverScrollMode(View.OVER_SCROLL_NEVER);

        final TextView textView = binding.textHome;
        homeViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}