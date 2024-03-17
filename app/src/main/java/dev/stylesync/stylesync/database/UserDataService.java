package dev.stylesync.stylesync.database;

import dev.stylesync.stylesync.MainActivity;
import dev.stylesync.stylesync.data.DataCallback;
import dev.stylesync.stylesync.data.UserData;

public class UserDataService {
    private final MainActivity context;

    public UserDataService(MainActivity context) {
        this.context = context;
    }

    public void getData(DataCallback callback) {
        new Thread(() -> {
            UserData userData = context.database.getUserData();
            callback.onDataReceived(userData);
        }).start();
    }
}
