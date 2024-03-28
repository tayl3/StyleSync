package dev.stylesync.stylesync.ui.settings;

import android.mtp.MtpConstants;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SettingsViewModel extends ViewModel {
    private final MutableLiveData<String> mText;
    private final MutableLiveData<Boolean> mIsAuthenticated; // Indicates if the user is authenticated
    private final MutableLiveData<String> mUsername;
    private final MutableLiveData<String> mUserId;

    public SettingsViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is settings fragment");
        mIsAuthenticated = new MutableLiveData<>();
        mIsAuthenticated.setValue(false);
        mUsername = new MutableLiveData<>();
        mUserId = new MutableLiveData<>();
    }

    public LiveData<String> getText() {
        return mText;
    }

    public LiveData<Boolean> getIsAuthenticated() {
        return mIsAuthenticated;
    }

    public LiveData<String> getUsername() {
        return mUsername;
    }
    public LiveData<String> getUserId() {
        return mUserId;
    }

    public void setAuthenticated(boolean isAuthenticated) {
        mIsAuthenticated.setValue(isAuthenticated);
    }

    public void setUsername(String username) {
        mUsername.setValue(username);
    }

    public void setUserId(String userId) {
        mUserId.setValue(userId);
    }
}