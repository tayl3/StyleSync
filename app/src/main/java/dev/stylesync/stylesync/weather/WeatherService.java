package dev.stylesync.stylesync.weather;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;

import androidx.core.app.ActivityCompat;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class WeatherService {
    private final Context context;
    private final LocationManager locationManager;
    private final String apiKey;

    public WeatherService(Context context, String apiKey) {
        this.context = context;
        this.locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        this.apiKey = apiKey;
    }

    private double[] getCoordinate() {
        // Try fine location
        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (location != null) {
                return new double[]{location.getLatitude(), location.getLongitude()};
            }
        }

        // Try coarse location
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            if (location != null) {
                return new double[]{location.getLatitude(), location.getLongitude()};
            }
        }

        // Location unavailable
        return null;
    }

    public void getWeatherData(WeatherDataCallback callback) {
        double[] coordinate = getCoordinate();
        if (coordinate == null){
            callback.onError("Location data unavailable");
            return;
        }
        double latitude = coordinate[0];
        double longitude = coordinate[1];

        new Thread(() -> {
            try {
                URL url = new URL("https://api.openweathermap.org/data/2.5/weather?lat=" + latitude + "&lon=" + longitude + "&appid=" + apiKey);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                try {
                    InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                    StringBuilder result = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        result.append(line);
                    }
                    callback.onDataReceived(result.toString());

                } finally {
                    urlConnection.disconnect();
                }
            } catch (Exception e) {
                callback.onError(e.toString());
            }
        }).start();
    }

    public interface WeatherDataCallback {
        void onDataReceived(String data);
        void onError(String error);
    }
}
