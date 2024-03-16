package dev.stylesync.stylesync;

import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import dev.stylesync.stylesync.databinding.ActivityMainBinding;
import dev.stylesync.stylesync.weather.WeatherService;
import dev.stylesync.stylesync.weather.WeatherService.WeatherDataCallback;

public class MainActivity extends AppCompatActivity {

    private static final String WEATHER_API_KEY = "8474ee05487a0d67588216334a9cc992";

    private ActivityMainBinding binding;

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

        // Weather service
        WeatherService weatherService = new WeatherService(this, WEATHER_API_KEY);

        // Callback when weather data available
        WeatherDataCallback callback = new WeatherDataCallback(){
            @Override
            public void onDataReceived(String data) {
                // JSON string, See https://openweathermap.org/current
                System.out.println(data);
            }

            @Override
            public void onError(String error) {
                System.err.println(error);
            }
        };
        // Call Weather API
        weatherService.getWeatherData(callback);
    }

    private void requestPermission(){
        // Location
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{
                    "android.permission.ACCESS_FINE_LOCATION",
                    "android.permission.ACCESS_COARSE_LOCATION"}, 0);
        }
    }
}