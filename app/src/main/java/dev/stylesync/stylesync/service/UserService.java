package dev.stylesync.stylesync.service;

import android.provider.Settings;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

import dev.stylesync.stylesync.MainActivity;
import dev.stylesync.stylesync.data.UserData;
import dev.stylesync.stylesync.utility.Database;

public class UserService implements Service {
    private static UserService user_instance = null;
    private final Database database;
    private final MainActivity context;
    private UserData userData;
    private final MutableLiveData<List<UserData.Cloth>> clothesLiveData = new MutableLiveData<>();

    public UserService(MainActivity context) {
        this.context = context;
        this.database = context.database;
        initUserData();
    }

    public static synchronized UserService getInstance(MainActivity context) {
        if (user_instance == null) {
            user_instance = new UserService(context);
        }
        return user_instance;
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
            if (this.userData == null) {
                this.userData = new UserData();
            }
        }
    }

    public LiveData<List<UserData.Cloth>> getClothesLiveData() {
        return clothesLiveData;
    }

    public UserData getUserData() {
        return userData;
    }


    public void addClothingItem(UserData.Cloth cloth) {
        List<UserData.Cloth> clothes = userData.getClothes();
        if (clothes != null) {
            clothes.add(cloth);
            System.out.println("Clothes descriptions: " + userData.getClothesDescriptions().toString());
            clothesLiveData.setValue(userData.getClothes());
        }
        database.setUserData(userData);
    }

    public void removeClothingItem(int index) {
        List<UserData.Cloth> clothes = userData.getClothes();
        if (clothes != null && index >= 0 && index < clothes.size()) {
            clothes.remove(index);
            clothesLiveData.setValue(userData.getClothes());
        }
        database.setUserData(userData);
    }

    public void saveUserData() {
        database.setUserData(userData);
    }
}
