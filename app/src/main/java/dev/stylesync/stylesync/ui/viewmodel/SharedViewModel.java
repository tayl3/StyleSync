package dev.stylesync.stylesync.ui.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import dev.stylesync.stylesync.ui.home.viewpager.ViewPagerItem;

public class SharedViewModel extends ViewModel {
    private final MutableLiveData<List<ViewPagerItem>> viewPagerItems = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>();

    public void setViewPagerItems(List<ViewPagerItem> items) {
        this.viewPagerItems.setValue(items);
    }

    public LiveData<List<ViewPagerItem>> getViewPagerItems() {
        return this.viewPagerItems;
    }

    public void setLoading(boolean loading) {
        isLoading.setValue(loading);
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }
}
