package dev.stylesync.stylesync.ui.home.viewpager;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

public class SharedViewModel extends ViewModel {
    private final MutableLiveData<List<ViewPagerItem>> viewPagerItems = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>();

    public LiveData<List<ViewPagerItem>> getViewPagerItems() {
        return this.viewPagerItems;
    }

    public void setViewPagerItems(List<ViewPagerItem> items) {
        this.viewPagerItems.setValue(items);
    }

    public void setLoading(boolean loading) {
        isLoading.setValue(loading);
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }
}
