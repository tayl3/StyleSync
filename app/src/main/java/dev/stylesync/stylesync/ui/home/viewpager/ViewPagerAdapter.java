package dev.stylesync.stylesync.ui.home.viewpager;

import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import java.util.List;

import dev.stylesync.stylesync.R;
import dev.stylesync.stylesync.ui.home.carousel.ImageAdapter;

public class ViewPagerAdapter extends RecyclerView.Adapter<ViewPagerAdapter.ViewHolder> {

    List<ViewPagerItem> itemList;

    public ViewPagerAdapter(List<ViewPagerItem> itemList) {
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
        ImageAdapter imageAdapter = new ImageAdapter(viewPagerItem.getImageUrls());
        holder.viewPagerImageCarousel.setAdapter(imageAdapter);

        holder.tvHeading.setText(viewPagerItem.getHeading());
        holder.tvDesc.setText(viewPagerItem.getDescription());

        holder.startAutoScroll();
    }

    @Override
    public void onViewRecycled(@NonNull ViewHolder holder) {
        super.onViewRecycled(holder);
        holder.stopAutoScroll();
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        static final long AUTO_SCROLL_INTERVAL = 2500;
        ViewPager2 viewPagerImageCarousel;
        TextView tvHeading, tvDesc;
        Handler handler = new Handler(Looper.getMainLooper());
        Runnable autoScrollRunnable;

        public ViewHolder(@NonNull View view) {
            super(view);
            viewPagerImageCarousel = view.findViewById(R.id.viewPagerImageCarousel);
            tvHeading = view.findViewById(R.id.tvHeading);
            tvDesc = view.findViewById(R.id.tvDesc);

            setupAutoScroll();
        }

        private void setupAutoScroll() {
            autoScrollRunnable = new Runnable() {
                @Override
                public void run() {
                    if (viewPagerImageCarousel.getAdapter() != null) {
                        int numItems = viewPagerImageCarousel.getAdapter().getItemCount();
                        if (numItems > 0) {
                            int currItem = viewPagerImageCarousel.getCurrentItem();
                            currItem = (currItem + 1) % numItems;
                            viewPagerImageCarousel.setCurrentItem(currItem, true);
                            handler.postDelayed(this, AUTO_SCROLL_INTERVAL);
                        }
                    }
                }
            };
        }

        void startAutoScroll() {
            stopAutoScroll();
            handler.postDelayed(autoScrollRunnable, AUTO_SCROLL_INTERVAL);
        }

        void stopAutoScroll() {
            handler.removeCallbacks(autoScrollRunnable);
        }
    }
}
