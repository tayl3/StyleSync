package dev.stylesync.stylesync.ui.wardrobe;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import dev.stylesync.stylesync.R;
import dev.stylesync.stylesync.data.UserData;

public class WardrobeItemAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<UserData.Cloth> items;
    private OnItemClickListener listener;
    private static final int VIEW_TYPE_ITEM = 0;
    private static final int VIEW_TYPE_EMPTY = 1;
    private List<UserData.Cloth> clothes;

    public interface OnItemClickListener {
        void onDeleteClick(int position);
    }

    public WardrobeItemAdapter(List<UserData.Cloth> items, OnItemClickListener listener) {
        this.items = items;
        this.listener = listener;
    }

    public void setClothes(List<UserData.Cloth> clothes) {
        this.clothes = clothes;
    }

    @Override
    public int getItemViewType(int position) {
        if (items.isEmpty()) {
            return VIEW_TYPE_EMPTY;
        } else {
            return VIEW_TYPE_ITEM;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_ITEM) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.wardrobe_item, parent, false);
            return new ItemViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.empty_wardrobe_view, parent, false);
            return new EmptyViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ItemViewHolder) {
            UserData.Cloth item = items.get(position);
            ((ItemViewHolder) holder).textViewItem.setText(item.getDescription());
            ((ItemViewHolder) holder).buttonDelete.setOnClickListener(v -> listener.onDeleteClick(position));
        }
    }

    @Override
    public int getItemCount() {
        return items.isEmpty() ? 1 : items.size();
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView textViewItem;
        Button buttonDelete;

        public ItemViewHolder(View itemView) {
            super(itemView);
            textViewItem = itemView.findViewById(R.id.wardrobe_item_text);
            buttonDelete = itemView.findViewById(R.id.delete_wardrobe_item_button);
        }
    }

    public static class EmptyViewHolder extends RecyclerView.ViewHolder {
        public EmptyViewHolder(View itemView) {
            super(itemView);
        }
    }
}
