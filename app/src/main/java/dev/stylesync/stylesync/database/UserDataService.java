package dev.stylesync.stylesync.database;

import dev.stylesync.stylesync.data.DataCallback;
import dev.stylesync.stylesync.data.UserData;

public class UserDataService {
    public void getData(DataCallback callback) {
        new Thread(() -> {
            UserData userData = new UserData();

            userData.userPreference.favorite_colors = new String[]{"red", "blue"};
            userData.userPreference.schedules = new String[]{"business conference"};
            userData.clothes = new String[]{
                    "White T-shirt",
                    "Black T-shirt",
                    "Blue T-shirt",
                    "Red T-shirt",
                    "Green T-shirt",
                    "Gray T-shirt",
                    "White Jeans",
                    "Blue Jeans",
                    "Black Jeans",
                    "Skinny Jeans",
                    "Bootcut Jeans",
                    "Ripped Jeans",
                    "Boxer Briefs",
                    "Briefs",
                    "Boxers",
                    "Thongs",
                    "Crew Socks",
                    "Ankle Socks",
                    "Dress Socks",
                    "No-show Socks",
                    "White Sneakers",
                    "Black Sneakers",
                    "Blue Sneakers",
                    "Red Sneakers",
                    "Running Shoes",
                    "Canvas Shoes",
                    "Leather Jacket",
                    "Denim Jacket",
                    "Bomber Jacket",
                    "Puffer Jacket",
                    "Hooded Sweatshirt",
                    "Crewneck Sweatshirt",
                    "V-neck Sweater",
                    "Cable-knit Sweater",
                    "Cashmere Sweater",
                    "Chinos",
                    "Khakis",
                    "Corduroy Pants",
                    "Cargo Pants",
                    "Track Pants",
                    "Wrap Dress",
                    "Shift Dress",
                    "Maxi Dress",
                    "A-line Skirt",
                    "Pencil Skirt",
                    "Denim Skirt",
                    "Silk Blouse",
                    "Button-up Blouse",
                    "Printed Blouse",
                    "Leather Belt",
                    "Fabric Belt",
                    "Braided Belt",
                    "Fedora Hat",
                    "Beanie Hat",
                    "Bucket Hat",
                    "Wide-brim Hat",
                    "Knit Scarf",
                    "Silk Scarf",
                    "Wool Scarf",
                    "Leather Gloves",
                    "Knit Gloves",
                    "Touchscreen Gloves",
                    "Digital Watch",
                    "Analog Watch",
                    "Smart Watch",
                    "Stud Earrings",
                    "Hoop Earrings",
                    "Dangle Earrings",
                    "Leather Bracelet",
                    "Beaded Bracelet",
                    "Chain Bracelet",
                    "Aviator Sunglasses",
                    "Wayfarer Sunglasses",
                    "Round Sunglasses",
                    "Cat-eye Sunglasses"
            };

            callback.onDataReceived(userData);
        }).start();
    }
}
