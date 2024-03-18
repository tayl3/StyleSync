package dev.stylesync.stylesync;

import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import dev.stylesync.stylesync.data.Data;
import dev.stylesync.stylesync.data.DataCallback;
import dev.stylesync.stylesync.databinding.ActivityMainBinding;
import dev.stylesync.stylesync.service.AIService;
import dev.stylesync.stylesync.service.UserDataService;
import dev.stylesync.stylesync.service.WeatherService;
import dev.stylesync.stylesync.utility.Database;

public class MainActivity extends AppCompatActivity {

    public static final String WEATHER_API_URL = "https://api.openweathermap.org/data/2.5/weather";
    public static final String WEATHER_API_KEY = "8474ee05487a0d67588216334a9cc992";
    public static final String CHATGPT_API_URL = "https://api.openai.com/v1/chat/completions";
    public static final String CHATGPT_API_KEY = "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";

    private ActivityMainBinding binding;

    // Services
    public AIService aiService;
    public WeatherService weatherService;
    public UserDataService userDataService;

    public Database database;
    private DataCallback planCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);

        // Request permission
        requestPermission();

        // Database
        database = new Database();

        // Services
        aiService = new AIService(this);
        weatherService = new WeatherService(this);
        userDataService = new UserDataService(this);

        // Callback function after receiving plan
        planCallback = new DataCallback() {
            @Override
            public void OnDataReceived(Data data) {
                System.out.println(data);
            }

            @Override
            public void OnError(String msg) {
                System.err.println(msg);
            }
        };

        // Infer plan once
        aiService.generatePlan(planCallback);
    }

    private void requestPermission() {
        // Location
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{
                    "android.permission.ACCESS_FINE_LOCATION",
                    "android.permission.ACCESS_COARSE_LOCATION"}, 0);
        }
    }
}