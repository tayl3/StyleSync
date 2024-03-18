package dev.stylesync.stylesync.service;

import java.util.List;

import dev.stylesync.stylesync.MainActivity;
import dev.stylesync.stylesync.data.UserData;
import dev.stylesync.stylesync.utility.Database;

public class UserDataService implements Service {
    private final Database database;
    private final UserData userData;

    public UserDataService(MainActivity context) {
        this.database = context.database;
        this.userData = database.getUserData();
    }

    public UserData getData() {
        return database.getUserData();
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
