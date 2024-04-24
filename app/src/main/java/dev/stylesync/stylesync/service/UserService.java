package dev.stylesync.stylesync.service;

import android.provider.Settings;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;

import dev.stylesync.stylesync.MainActivity;
import dev.stylesync.stylesync.data.UserData;
import dev.stylesync.stylesync.utility.Database;

public class UserService implements Service {
    private final Database database;
    private UserData userData;
    private final MainActivity context;

    private static UserService user_instance = null;
    public static synchronized UserService getInstance(MainActivity context) {
        if(user_instance == null) {
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
        //Log.d("GoogleID", googleId);

        if (googleId != null) {
            this.userData = database.getUserData(googleId);
            Log.d("GoogleID", googleId);
        } else {
            String deviceId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
            Log.d("UserService", "GoogleID is empty. Using Android ID: " + deviceId);

            this.userData = database.getUserData(deviceId);
            Log.d("deviceID", deviceId);
            if (this.userData == null){
                this.userData = new UserData();
            }
        }
    }

    public UserData getUserData() {
        return userData;
    }

    public void setUserData(UserData userData) {
        this.userData = userData;
    }

    public void saveUserData(){
        database.setUserData(userData);
    }
}