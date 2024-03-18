package dev.stylesync.stylesync.database;

import dev.stylesync.stylesync.MainActivity;
import dev.stylesync.stylesync.data.UserData;

public class UserDataService {
    private final MainActivity context;
    private final Database database;

    public UserDataService(MainActivity context) {
        this.context = context;
        this.database = context.database;
    }

    public UserData getData() {
        return database.getUserData();
    }
}
