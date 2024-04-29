package dev.stylesync.stylesync.ui.wardrobe;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import dev.stylesync.stylesync.MainActivity;
import dev.stylesync.stylesync.R;
import dev.stylesync.stylesync.data.UserData;
import dev.stylesync.stylesync.databinding.FragmentWardrobeBinding;
import dev.stylesync.stylesync.service.UserService;

public class WardrobeFragment extends Fragment {


    private FragmentWardrobeBinding binding;
    private RecyclerView recyclerView;
    private WardrobeItemAdapter adapter;
    private UserService userService;
    private EditText inputClothes;
    private Button addClothesButton;
    private TextView emptyView;

    public static WardrobeFragment newInstance() {
        return new WardrobeFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentWardrobeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        userService = UserService.getInstance((MainActivity) getActivity());
        recyclerView = root.findViewById(R.id.wardrobe_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        inputClothes = root.findViewById(R.id.add_clothes);
        addClothesButton = root.findViewById(R.id.add_clothes_button);
        emptyView = root.findViewById(R.id.empty_wardrobe_view_text);

        ActivityResultLauncher<Intent> itemActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        boolean delete = result.getData().getBooleanExtra("delete", false);
                        int position = result.getData().getIntExtra("position", -1);
                        if (delete) {
                            userService.removeClothingItem(position);
                        }
                    }
                });

        userService.getClothesLiveData().observe(getViewLifecycleOwner(), clothes -> {
            if (adapter == null) {
                adapter = new WardrobeItemAdapter(clothes, position -> {
                    showEmptyView(adapter.getItemCount() == 0);

                    Intent intent = new Intent(getActivity(), WardrobeItemActivity.class);
                    intent.putExtra("description", clothes.get(position).getDescription());
                    intent.putExtra("url", clothes.get(position).getUrl());
                    intent.putExtra("position", position);

                    itemActivityResultLauncher.launch(intent);

                    if (clothes.isEmpty()) {
                        adapter.notifyDataSetChanged();
                    }
                });
                recyclerView.setAdapter(adapter);
            } else {
                adapter.setClothes(clothes);
                adapter.notifyDataSetChanged();
            }
            showEmptyView(clothes.isEmpty());
        });

        addClothesButton.setOnClickListener(v -> {
            String clothingItem = inputClothes.getText().toString().trim();
            if (!clothingItem.isEmpty()) {
                UserData.Cloth cloth = new UserData.Cloth(clothingItem, "", new ArrayList<>());
                userService.addClothingItem(cloth);
                inputClothes.setText("");
            }
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void showEmptyView(boolean show) {
        emptyView.setVisibility(show ? View.VISIBLE : View.GONE);
        recyclerView.setVisibility(show ? View.GONE : View.VISIBLE);
    }
}