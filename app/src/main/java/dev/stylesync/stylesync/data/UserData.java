package dev.stylesync.stylesync.data;

import java.util.ArrayList;
import java.util.List;

public class UserData implements Data {
    private List<String> clothes = new ArrayList<>();
    private UserPreference userPreference = new UserPreference();

    public List<String> getClothes() {
        return clothes;
    }

    public void setClothes(List<String> clothes) {
        this.clothes = clothes;
    }

    public UserPreference getUserPreference() {
        return userPreference;
    }

    public void setUserPreference(UserPreference userPreference) {
        this.userPreference = userPreference;
    }

    public static class UserPreference {
        private List<String> favoriteColors;
        private List<String> schedules;

        public List<String> getFavoriteColors() {
            return favoriteColors;
        }

        public void setFavoriteColors(List<String> favoriteColors) {
            this.favoriteColors = favoriteColors;
        }

        public List<String> getSchedules() {
            return schedules;
        }

        public void setSchedules(List<String> schedules) {
            this.schedules = schedules;
        }
    }
}