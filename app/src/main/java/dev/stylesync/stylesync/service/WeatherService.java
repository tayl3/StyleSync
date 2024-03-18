package dev.stylesync.stylesync.service;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;

import androidx.core.app.ActivityCompat;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import dev.stylesync.stylesync.MainActivity;
import dev.stylesync.stylesync.data.WeatherData;

public class WeatherService implements Service {
    private final MainActivity context;
    private final LocationManager locationManager;
    private WeatherData weatherData;

    public WeatherService(MainActivity context) {
        this.context = context;
        this.locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
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

    public WeatherData getWeatherData() {
        Thread thread = new Thread(() -> {
            double[] coordinate = getCoordinate();
            if (coordinate == null) {
                System.err.println("Location data unavailable");
                weatherData = null;
                return;
            }
            double latitude = coordinate[0];
            double longitude = coordinate[1];

            try {
                URL url = new URL(MainActivity.WEATHER_API_URL + "?lat=" + latitude + "&lon=" + longitude + "&appid=" + MainActivity.WEATHER_API_KEY);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                StringBuilder result = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }
                weatherData = parseWeatherData(result.toString());
                urlConnection.disconnect();
            } catch (IOException e) {
                System.err.println("Network connection failed");
                weatherData = null;
            }
        });
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
        }

        return weatherData;
    }


    // See https://openweathermap.org/current
    private WeatherData parseWeatherData(String JSONData) {
        try {
            JSONObject obj = new JSONObject(JSONData);
            WeatherData data = new WeatherData();
            data.getTemperature().setTemp(obj.getJSONObject("main").getDouble("temp"));
            data.getTemperature().setTempFeelsLike(obj.getJSONObject("main").getDouble("feels_like"));
            data.getTemperature().setTempMin(obj.getJSONObject("main").getDouble("temp_min"));
            data.getTemperature().setTempMax(obj.getJSONObject("main").getDouble("temp_max"));
            data.setHumidity(obj.getJSONObject("main").getDouble("humidity"));
            data.setWindSpeed(obj.getJSONObject("wind").getDouble("speed"));
            data.setWeather(obj.getJSONArray("weather").getJSONObject(0).getString("main"));
            return data;
        } catch (Exception e) {
            System.err.println("Invalid weather JSON data: " + e);
            return null;
        }
    }
}
