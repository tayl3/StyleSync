package dev.stylesync.stylesync.data;

import java.util.List;

public class UserData implements Data {
    public List<String> clothes;
    public UserPreference userPreference = new UserPreference();

    public static class UserPreference {
        public List<String> favorite_colors;
        public List<String> schedules;
    }
}