package dev.stylesync.stylesync.data;

public class UserData implements Data {
    public String[] clothes;
    public UserPreference userPreference = new UserPreference();

    public static class UserPreference {
        public String[] favorite_colors;
        public String[] schedules;
    }
}