package dev.stylesync.stylesync;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Base64;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;

import dev.stylesync.stylesync.databinding.ActivityMainBinding;
import dev.stylesync.stylesync.service.ImageService;
import dev.stylesync.stylesync.data.PlanData;
import dev.stylesync.stylesync.service.PlanService;
import dev.stylesync.stylesync.service.UserService;
import dev.stylesync.stylesync.service.WeatherService;
import dev.stylesync.stylesync.utility.Database;

public class MainActivity extends AppCompatActivity {
    private static final long UPDATE_INTERVAL_MS = 16;

    // Request codes
    public static final int PERMISSION_CODE_LOCATION = 1;
    public static final int PERMISSION_CODE_CAMERA = 2;
    public static final int REQUEST_CODE_CAPTURE_IMAGE = 1;

    private ActivityMainBinding binding;

    private final Handler uiHandler = new Handler(Looper.getMainLooper());

    // Services
    public PlanService planService;
    public WeatherService weatherService;
    public UserService userService;
    public ImageService imageService;

    public Database database;
    private boolean generatingPlan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        BottomNavigationView navView = findViewById(R.id.nav_view);
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_settings)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);

        // Request permission
        requestPermission();

        // Database
        database = new Database();

        // Initialize Services
        planService = new PlanService(this);
        weatherService = new WeatherService(this);
        userService = new UserService(this);
        imageService = new ImageService(this);

        imageService.captureImage();
    }

    public void generatePlan(View view) {
        if (!generatingPlan) {
            setPlanText("Generating Plan...");
            generatingPlan = true;
            planService.generatePlan(new PlanService.PlanDataCallback() {
                @Override
                public void onPlanDataFetched(PlanData planData) {
                    String text = "Plan 1: " + Arrays.toString(planData.getPlan1()) + "\n\n" +
                            "Plan 2: " + Arrays.toString(planData.getPlan2()) + "\n\n" +
                            "Plan 3: " + Arrays.toString(planData.getPlan3());
                    setPlanText(text);
                    generatingPlan = false;
                }

                @Override
                public void onError(String message) {
                    setPlanText("Failed to generate plan. Please try again.");
                    generatingPlan = false;
                }
            });
        }
    }

    private void setPlanText(final String text) {
        runOnUiThread(() -> {
            TextView textView = findViewById(R.id.text_home);
            textView.setText(text);
        });
    }

    private void requestPermission() {
        // Location
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{
                    android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_CODE_LOCATION);
        }
        // Camera
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{
                    "android.permission.CAMERA"}, PERMISSION_CODE_CAMERA);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_CAPTURE_IMAGE) {
            if (resultCode != RESULT_OK) {
                System.err.println("Failed to request image capture");
                return;
            }
            Bundle extras = data.getExtras();
            if (extras == null || extras.get("data") == null) {
                System.err.println("Failed to capture the image");
                return;
            }
            System.out.println("Image captured");
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
            byte[] byteArray = byteArrayOutputStream.toByteArray();
            String imageBase64 = Base64.encodeToString(byteArray, Base64.DEFAULT);
            imageService.uploadImage(imageBase64);
        }
    }
}
