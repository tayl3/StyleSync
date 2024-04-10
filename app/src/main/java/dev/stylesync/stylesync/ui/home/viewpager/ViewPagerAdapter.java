package dev.stylesync.stylesync.ui.home.viewpager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import dev.stylesync.stylesync.R;

public class ViewPagerAdapter extends RecyclerView.Adapter<ViewPagerAdapter.ViewHolder> {

    ArrayList<ViewPagerItem> itemList;

    public ViewPagerAdapter(ArrayList<ViewPagerItem> itemList) {
        this.itemList = itemList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.viewpager_item, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ViewPagerItem viewPagerItem = itemList.get(position);

        holder.imageView.setImageResource(viewPagerItem.getImageId());
        holder.tvHeading.setText(viewPagerItem.getHeading());
        holder.tvDesc.setText(viewPagerItem.getDescription());
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView tvHeading, tvDesc;

        public ViewHolder(@NonNull View view) {
            super(view);

            imageView = view.findViewById(R.id.ivImage);
            tvHeading = view.findViewById(R.id.tvHeading);
            tvDesc = view.findViewById(R.id.tvDesc);
        }
    }
}
