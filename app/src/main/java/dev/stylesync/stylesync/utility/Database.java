package dev.stylesync.stylesync.utility;

import android.util.Log;

import java.sql.Array;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;

import dev.stylesync.stylesync.BuildConfig;
import dev.stylesync.stylesync.data.UserData;

public class Database {
    private final String database = "supabase";
    private final String userDataTable = "userdata";
    private String postgresUrl = BuildConfig.POSTGRES_URL;
    private Connection conn;
    private boolean status;
    private UserData userData;
    private static Database db_instance = null;

    public Database() {
        connect();
        System.out.println("Database connection: " + status);
    }

    public static synchronized Database getInstance() {
        if(db_instance == null) {
            db_instance = new Database();
        }
        return db_instance;
    }

    private void connect() {
        Thread thread = new Thread(() -> {
            try {
                Class.forName("org.postgresql.Driver");
                checkAndCreateDatabase();
                conn = DriverManager.getConnection(postgresUrl);
                checkAndCreateTable();
                //initSampleUserData();
                status = true;
            } catch (Exception e) {
                status = false;
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
        try (Connection conn = DriverManager.getConnection(postgresUrl)) {
            System.out.println("Database '" + database + "' exists.");
        } catch (SQLException e) {
            createDatabase();
        }
    }

    private void createDatabase() {
        String sql = "CREATE DATABASE " + database;

        try (Connection conn = DriverManager.getConnection(postgresUrl);
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(sql);
            System.out.println("Database '" + database + "' has been created.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void checkAndCreateTable() {
        String sql = "CREATE TABLE " + userDataTable + " (id VARCHAR PRIMARY KEY, clothes TEXT[], favorite_colors TEXT[], schedules TEXT[])";

        try {
            DatabaseMetaData dbm = conn.getMetaData();
            // Check if "userdata" table exists
            try (ResultSet tables = dbm.getTables(null, null, userDataTable, null)) {
                if (tables.next()) {
                    // Table exists
                    System.out.println("Table '" + userDataTable + "' exists.");
                } else {
                    // Table does not exist
                    try (Statement stmt = conn.createStatement()) {
                        stmt.execute(sql);
                        System.out.println("Table '" + userDataTable + "' has been created.");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void setUserData(UserData userData) {
        String sql = "INSERT INTO " + userDataTable + " (id, clothes, favorite_colors, schedules) VALUES ('" + userData.getUserId() + "', ?, ?, ?) " +
                "ON CONFLICT (id) DO UPDATE SET clothes = EXCLUDED.clothes, favorite_colors = EXCLUDED.favorite_colors, schedules = EXCLUDED.schedules";

        Thread thread = new Thread(() -> {
            try {
                PreparedStatement stmt = conn.prepareStatement(sql);

                Array clothesArray = conn.createArrayOf("text", userData.getClothes().toArray());
                Array colorsArray = conn.createArrayOf("text", userData.getUserPreference().getFavoriteColors().toArray());
                Array schedulesArray = conn.createArrayOf("text", userData.getUserPreference().getSchedules().toArray());

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

    public UserData getUserData(String userId) {
        Log.d("getUserData", "Getting user data for userId: " + userId);
        String sql = "SELECT clothes, favorite_colors, schedules FROM " + userDataTable + " WHERE id = '" + userId + "';";

        Thread thread = new Thread(() -> {
            UserData userData = new UserData();
            userData.setUserId(userId);
            try {
                Log.d("getUserData", "preparing statement: " + sql);
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    Array clothesArray = rs.getArray("clothes");
                    Array colorsArray = rs.getArray("favorite_colors");
                    Array schedulesArray = rs.getArray("schedules");

                    if (clothesArray != null)
                        userData.setClothes(Arrays.asList((String[]) clothesArray.getArray()));
                    if (colorsArray != null)
                        userData.getUserPreference().setFavoriteColors(Arrays.asList((String[]) colorsArray.getArray()));
                    if (schedulesArray != null)
                        userData.getUserPreference().setSchedules(Arrays.asList((String[]) schedulesArray.getArray()));
                } else {
                    // User does not exist, create a new user
                    userData.setClothes(new ArrayList<>());
                    userData.getUserPreference().setFavoriteColors(new ArrayList<>());
                    userData.getUserPreference().setSchedules(new ArrayList<>());
                    setUserData(userData);
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

    public void dropDatabase(String database) {
        String sql = "DROP DATABASE IF EXISTS " + database;

        try (Connection conn = DriverManager.getConnection(postgresUrl);
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(sql);
            System.out.println("Database '" + database + "' has been dropped.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void dropTable(String table) {
        String sql = "DROP TABLE IF EXISTS " + table;

        try {
            Statement stmt = conn.createStatement();
            stmt.execute(sql);
            System.out.println("Table '" + table + "' has been dropped.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void initSampleUserData() {
        UserData userData = new UserData();
        userData.getUserPreference().setFavoriteColors(Arrays.asList("red","blue"));
        userData.getUserPreference().setSchedules(Arrays.asList("hiking"));
        userData.setClothes(Arrays.asList(
                "Red T-shirt",
                "Blue Jeans",
                "Black Dress",
                "White Sneakers",
                "Gray Hoodie",
                "Brown Leather Jacket",
                "Black Ankle Boots",
                "Green Cargo Pants",
                "Navy Blazer",
                "Striped Sweater",
                "Khaki Trousers",
                "Denim Jacket",
                "Plaid Shirt",
                "Beige Scarf",
                "Leather Belt",
                "White Button-down Shirt",
                "Black Leggings",
                "Yellow Raincoat",
                "Wool Coat",
                "Silk Tie",
                "Sports Cap",
                "Running Shoes",
                "Suede Gloves",
                "Linen Shorts",
                "Floral Dress",
                "Bikini Swimwear",
                "Cashmere Scarf",
                "Sunglasses",
                "Canvas Backpack",
                "Gold Necklace",
                "Silver Earrings",
                "Watch",
                "Purple Cardigan",
                "Orange Polo Shirt",
                "Pink Tank Top",
                "High Heels",
                "Flip Flops",
                "Black Tuxedo",
                "Bow Tie",
                "Cufflinks",
                "Pajama Set",
                "Bathrobe",
                "Beanie Hat",
                "Winter Boots",
                "Gym Shorts",
                "Yoga Pants",
                "Compression Shirt",
                "Sports Bra",
                "Cycling Jersey",
                "Hiking Boots"
        ));
        setUserData(userData);
    }
}
