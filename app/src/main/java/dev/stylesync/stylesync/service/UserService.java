package dev.stylesync.stylesync.service;

import android.provider.Settings;
import android.util.Log;

import java.util.List;

import dev.stylesync.stylesync.MainActivity;
import dev.stylesync.stylesync.data.UserData;
import dev.stylesync.stylesync.ui.settings.SettingsViewModel;
import dev.stylesync.stylesync.utility.Database;

public class UserService implements Service {
    private final Database database;
    private final UserData userData;
    private final SettingsViewModel settingsViewModel = new SettingsViewModel();

    private static UserService user_instance = null;
    public static synchronized UserService getInstance(MainActivity context) {
        if(user_instance == null) {
            user_instance = new UserService(context);
        }
        return user_instance;
    }

    public UserService(MainActivity context) {
        this.database = context.database;

        String googleId = settingsViewModel.getUserId().getValue();

        if (googleId != null) {
            this.userData = database.getUserData(googleId);
            Log.d("GoogleID", googleId);
        } else {
            String deviceId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
            Log.d("UserService", "GoogleID is empty. Using Android ID: " + deviceId);

            this.userData = database.getUserData(deviceId);
            Log.d("deviceID", deviceId);
        }
    }

    public UserData getUserData() {
        return userData;
    }

    public void addElement(List<String> list, String elem) {
        list.add(elem);
        database.setUserData(userData);
    }

    public void removeElement(List<String> list, int index) {
        list.remove(index);
        database.setUserData(userData);
    }

    public void removeElement(List<String> list, String elem) {
        list.remove(elem);
        database.setUserData(userData);
    }
}
