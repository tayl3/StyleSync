package dev.stylesync.stylesync.database;

import java.sql.Array;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import dev.stylesync.stylesync.data.UserData;

public class Database {
    private final String host = "127.0.0.1";
    private final int port = 5432;
    private final String user = "postgres";
    private final String pass = "";
    private final String database = "stylesync";
    private String url = "jdbc:postgresql://%s:%d/";
    private Connection conn;
    private boolean status;
    private UserData userData;

    public Database() {
        this.url = String.format(this.url, this.host, this.port);
        connect();
        System.out.println("Database connection: " + status);
    }

    private void connect() {
        Thread thread = new Thread(() -> {
            try {
                Class.forName("org.postgresql.Driver");
                checkAndCreateDatabase();
                conn = DriverManager.getConnection(url + database, user, pass);
                checkAndCreateTable();
                initSampleUserData();
                status = true;
            } catch (Exception e) {
                status = false;
                System.out.print(e.getMessage());
                e.printStackTrace();
            }
        });
        thread.start();
        try {
            thread.join();
        } catch (Exception e) {
            e.printStackTrace();
            status = false;
        }
    }

    private void checkAndCreateDatabase() {
        try (Connection conn = DriverManager.getConnection(url + database, user, pass)) {
            System.out.println("Database '" + database + "' exists.");
        } catch (SQLException e) {
            createDatabase();
            System.out.println("Database '" + database + "' has been created.");
        }
    }

    private void createDatabase() {
        String sql = "CREATE DATABASE " + database;

        try (Connection conn = DriverManager.getConnection(url + "postgres", user, pass);
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void checkAndCreateTable() {
        String sql = "CREATE TABLE userdata (id SERIAL PRIMARY KEY, clothes TEXT[], favorite_colors TEXT[], schedules TEXT[])";

        try {
            DatabaseMetaData dbm = conn.getMetaData();
            // Check if "userdata" table exists
            try (ResultSet tables = dbm.getTables(null, null, "userdata", null)) {
                if (tables.next()) {
                    // Table exists
                    System.out.println("Table 'userdata' exists.");
                } else {
                    // Table does not exist
                    try (Statement stmt = conn.createStatement()) {
                        stmt.execute(sql);
                        System.out.println("Table 'userdata' has been created.");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void setUserData(UserData userData) {
        String sql = "INSERT INTO userdata (id, clothes, favorite_colors, schedules) VALUES (1, ?, ?, ?) " +
                "ON CONFLICT (id) DO UPDATE SET clothes = EXCLUDED.clothes, favorite_colors = EXCLUDED.favorite_colors, schedules = EXCLUDED.schedules";

        Thread thread = new Thread(() -> {
            try {
                PreparedStatement stmt = conn.prepareStatement(sql);

                Array clothesArray = conn.createArrayOf("text", userData.clothes);
                Array colorsArray = conn.createArrayOf("text", userData.userPreference.favorite_colors);
                Array schedulesArray = conn.createArrayOf("text", userData.userPreference.schedules);

                stmt.setArray(1, clothesArray);
                stmt.setArray(2, colorsArray);
                stmt.setArray(3, schedulesArray);

                stmt.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
        }
    }

    public UserData getUserData() {
        String sql = "SELECT clothes, favorite_colors, schedules FROM userdata WHERE id = 1";

        Thread thread = new Thread(() -> {
            UserData userData = new UserData();
            try {
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    Array clothesArray = rs.getArray("clothes");
                    Array colorsArray = rs.getArray("favorite_colors");
                    Array schedulesArray = rs.getArray("schedules");

                    if (clothesArray != null) userData.clothes = (String[]) clothesArray.getArray();
                    if (colorsArray != null)
                        userData.userPreference.favorite_colors = (String[]) colorsArray.getArray();
                    if (schedulesArray != null)
                        userData.userPreference.schedules = (String[]) schedulesArray.getArray();
                }
                this.userData = userData;
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
        }

        return userData;
    }

    public void dropDatabase() {
        String sql = "DROP DATABASE IF EXISTS " + database;

        try (Connection conn = DriverManager.getConnection(url + "postgres", user, pass);
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(sql);
            System.out.println("Database '" + database + "' has been dropped.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void dropTable() {
        String sql = "DROP TABLE IF EXISTS userdata";

        try {
            Statement stmt = conn.createStatement();
            stmt.execute(sql);
            System.out.println("Table 'userdata' has been dropped.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void initSampleUserData() {
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
        setUserData(userData);
    }
}
