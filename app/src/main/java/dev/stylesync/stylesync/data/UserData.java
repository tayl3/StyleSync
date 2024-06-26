package dev.stylesync.stylesync.data;

import androidx.annotation.NonNull;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class UserData implements Data {
    private String userId;
    private List<Cloth> clothes = new ArrayList<>();
    private UserPreference userPreference = new UserPreference();

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public List<Cloth> getClothes() {
        return clothes;
    }

    public void setClothes(List<Cloth> clothes) {
        this.clothes = clothes;
    }

    public List<String> getClothesJSON() {
        List<String> list = new ArrayList<>();
        for (Cloth cloth : clothes) {
            System.out.println(cloth);
            list.add(cloth.toString());
        }
        return list;
    }

    public void setClothesJSON(List<String> clothes) {
        List<Cloth> list = new ArrayList<>();
        for (String str : clothes) {
            System.out.println(str);
            list.add(new Gson().fromJson(str, Cloth.class));
        }
        this.clothes = list;
    }

    public List<String> getClothesDescriptions() {
        return clothes.stream().map(UserData.Cloth::getDescription).collect(Collectors.toList());
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
        private String celebrity;

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

        public String getCelebrity() {
            return celebrity;
        }

        public void setCelebrity(String celebrity) {
            this.celebrity = celebrity;
        }
    }

    public static class Cloth {
        private String description;
        private String url;
        private List<String> tags;

        public Cloth(String description, String url, List<String> tags) {
            this.description = description;
            this.url = url;
            this.tags = tags;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public List<String> getTags() {
            return tags;
        }

        public void setTags(List<String> tags) {
            this.tags = tags;
        }

        @NonNull
        @Override
        public String toString() {
            return new Gson().toJson(this);
        }
    }
}