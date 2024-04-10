package dev.stylesync.stylesync;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;

import dev.stylesync.stylesync.data.Data;
import dev.stylesync.stylesync.data.DataCallback;
import dev.stylesync.stylesync.data.ImgBBData;
import dev.stylesync.stylesync.service.ImageService;
import dev.stylesync.stylesync.data.PlanData;
import dev.stylesync.stylesync.data.StringCallback;
import dev.stylesync.stylesync.databinding.ActivityMainBinding;
import dev.stylesync.stylesync.service.ImageService;
import dev.stylesync.stylesync.service.PlanService;
import dev.stylesync.stylesync.service.UserService;
import dev.stylesync.stylesync.service.WeatherService;
import dev.stylesync.stylesync.utility.Database;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;

    // Services
    public PlanService planService;
    public WeatherService weatherService;
    public UserService userService;
    public ImageService imageService;

    public Database database;

    public RequestQueue volleyRequestQueue;
    // States
    private boolean generatingPlan;
    private boolean detectingImage;

    public static final int REQUEST_CODE_CAPTURE_IMAGE = 1;
    public static final int PERMISSION_CODE_LOCATION = 1;
    public static final int PERMISSION_CODE_CAMERA = 2;

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

        volleyRequestQueue = Volley.newRequestQueue(this);
    }

    public void generatePlan(View view) {
        if (generatingPlan) {
            return;
        }
        setPlanText("Generating Plan...");
        generatingPlan = true;
        planService.generatePlan(new DataCallback() {
            @Override
            public void onDataReceived(Data data) {
                PlanData planData = (PlanData) data;
                String text = "Plan 1: " + Arrays.toString(planData.getPlan1()) + "\n\n" +
                        "Plan 2: " + Arrays.toString(planData.getPlan2()) + "\n\n" +
                        "Plan 3: " + Arrays.toString(planData.getPlan3());
                setPlanText(text);
                generatingPlan = false;
            }

            @Override
            public void onError(String message) {
                System.err.println(message);
                setPlanText("Failed to generate plan. Please try again.");
                generatingPlan = false;
            }
        });
    }

    public void captureImage(View view){
        if (detectingImage){
            return;
        }
        detectingImage = true;
        setPlanText("Capturing Image...");
        imageService.captureImage();
    }

    private void onImageReceived(String imageBase64){
        setPlanText("Detecting Clothing Item...");
        imageService.uploadImage(imageBase64, new DataCallback() {
            @Override
            public void onDataReceived(Data data) {
                ImgBBData imgBBData = (ImgBBData) data;
                if (!imgBBData.isSuccess()) {
                    System.err.println("Image upload unsuccessful");
                    return;
                }
                System.out.println("Image URL: " + imgBBData.getData().getUrl());
                imageService.identifyImage(imgBBData.getData().getUrl(), new StringCallback() {
                    @Override
                    public void onStringReceived(String string) {
                        detectingImage = false;
                        if (string.equals("False")){
                            setPlanText("No Clothing Item Detected");
                            return;
                        }
                        if (string.equals("Multiple")){
                            setPlanText("Multiple Clothing Items Detected");
                            return;
                        }
                        setPlanText("Clothing Item Detected:\n" + string);
                    }

                    @Override
                    public void onError(String message) {
                        detectingImage = false;
                        setPlanText(message);
                        System.out.println(message);
                    }
                });
            }

            @Override
            public void onError(String message) {
                System.err.println(message);
            }
        });
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
                    Manifest.permission.CAMERA}, PERMISSION_CODE_CAMERA);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_CAPTURE_IMAGE) {
            if (resultCode != RESULT_OK) {
                setPlanText("No Image Captured");
                System.err.println("No image captured");
                detectingImage = false;
                return;
            }
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
            byte[] byteArray = byteArrayOutputStream.toByteArray();
            String imageBase64 = Base64.encodeToString(byteArray, Base64.DEFAULT);
            onImageReceived(imageBase64);
        }
    }
}
