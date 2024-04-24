package dev.stylesync.stylesync.service;

import android.provider.Settings;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

import dev.stylesync.stylesync.MainActivity;
import dev.stylesync.stylesync.data.ListType;
import dev.stylesync.stylesync.data.UserData;
import dev.stylesync.stylesync.ui.settings.SettingsViewModel;
import dev.stylesync.stylesync.utility.Database;

public class UserService implements Service {
    private final Database database;
    private UserData userData;
    private final MainActivity context;

    private MutableLiveData<List<String>> clothesLiveData = new MutableLiveData<>();

    private static UserService user_instance = null;

    public static synchronized UserService getInstance(MainActivity context) {
        if (user_instance == null) {
            user_instance = new UserService(context);
        }
        return user_instance;
    }

    public UserService(MainActivity context) {
        this.context = context;
        this.database = context.database;
        initUserData();
    }

    public void initUserData() {
        String googleId = FirebaseAuth.getInstance().getCurrentUser() != null ? FirebaseAuth.getInstance().getCurrentUser().getUid() : null;

        if (googleId != null) {
            this.userData = database.getUserData(googleId);
            clothesLiveData.postValue(userData.getClothes());
            Log.d("GoogleID", googleId);
        } else {
            String deviceId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
            Log.d("UserService", "GoogleID is empty. Using Android ID: " + deviceId);

            this.userData = database.getUserData(deviceId);
            clothesLiveData.postValue(userData.getClothes());
            Log.d("deviceID", deviceId);
            if (this.userData == null){
                this.userData = new UserData();
            }
        }
    }

    public LiveData<List<String>> getClothesLiveData() {
        return clothesLiveData;
    }

    public UserData getUserData() {
        return userData;
    }

    public void addElement(ListType type, String elem) {
        List<String> targetList = getListByType(type);
        if (targetList != null) {
            targetList.add(elem);
            if (type == ListType.CLOTHES) {
                clothesLiveData.setValue(targetList);
            }
            database.setUserData(userData);
        }
    }

    public void removeElement(ListType type, int index) {
        List<String> targetList = getListByType(type);
        if (targetList != null && index >= 0 && index < targetList.size()) {
            targetList.remove(index);
            if (type == ListType.CLOTHES) {
                clothesLiveData.setValue(targetList);
            }
            database.setUserData(userData);
        }
    }

    private List<String> getListByType(ListType type) {
        switch (type) {
            case CLOTHES:
                return userData.getClothes();
            case FAVORITE_COLORS:
                return userData.getUserPreference().getFavoriteColors();
            case SCHEDULES:
                return userData.getUserPreference().getSchedules();
            default:
                return null;
        }
    }

    public void setUserData(UserData userData) {
        this.userData = userData;
        clothesLiveData.postValue(userData.getClothes());
    }

    public void saveUserData(){
        database.setUserData(userData);
    }
}
