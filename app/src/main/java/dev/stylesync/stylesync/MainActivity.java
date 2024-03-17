package dev.stylesync.stylesync;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import dev.stylesync.stylesync.ai.AIService;
import dev.stylesync.stylesync.data.PlanData;
import dev.stylesync.stylesync.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
 
    public static final String WEATHER_API_URL = "https://api.openweathermap.org/data/2.5/weather";
    public static final String WEATHER_API_KEY = "8474ee05487a0d67588216334a9cc992";
    public static final String CHATGPT_API_URL = "https://api.openai.com/v1/chat/completions";
    public static final String CHATGPT_API_KEY = "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";

    private ActivityMainBinding binding;


    private AIService aiService;
    private Handler planHandler;
    public PlanData planData;
    public boolean planDataAvailable;

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

        // AI Service
        aiService = new AIService(this);

        // Create plan handler to receive plan result
        HandlerThread handlerThread = new HandlerThread("HandlerThread");
        handlerThread.start();
        planHandler = new Handler(handlerThread.getLooper());
        planHandler.post(planRunnable);

        // Infer plan once
        aiService.infer();
    }

    private void requestPermission(){
        // Location
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{
                    "android.permission.ACCESS_FINE_LOCATION",
                    "android.permission.ACCESS_COARSE_LOCATION"}, 0);
        }
    }

    private final Runnable planRunnable = new Runnable() {
        @Override
        public void run() {
            if (planDataAvailable){
                System.out.println(planData);
                planDataAvailable = false;
            }
            planHandler.postDelayed(this, 100);
        }
    };
}