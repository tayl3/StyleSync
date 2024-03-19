package dev.stylesync.stylesync.ui.settings;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SettingsViewModel extends ViewModel {
    // TODO: Implement the ViewModel
    private final MutableLiveData<String> mText;
    private final MutableLiveData<Boolean> mIsAuthenticated; // Indicates if the user is authenticated


    public SettingsViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is settings fragment");
        mIsAuthenticated = new MutableLiveData<>();
        mIsAuthenticated.setValue(false);
    }

    public LiveData<String> getText() {
        return mText;
    }

    public LiveData<Boolean> getIsAuthenticated() {
        return mIsAuthenticated;
    }

    public void setAuthenticated(boolean isAuthenticated) {
        mIsAuthenticated.setValue(isAuthenticated);
    }

}